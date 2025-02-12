package com.eeos.rocatrun.presentation

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.eeos.rocatrun.R
import com.eeos.rocatrun.service.LocationForegroundService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.Asset
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import com.eeos.rocatrun.component.CircularItemGauge
import com.eeos.rocatrun.viewmodel.GameViewModel
import com.eeos.rocatrun.util.FormatUtils
import android.os.PowerManager
import android.os.SystemClock
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.platform.LocalContext
import com.eeos.rocatrun.receiver.SensorUpdateReceiver
import com.eeos.rocatrun.viewmodel.MultiUserScreen
import com.eeos.rocatrun.viewmodel.MultiUserViewModel

class RunningActivity : ComponentActivity(), SensorEventListener {
    private val gameViewModel: GameViewModel by viewModels()
    private val multiUserViewModel: MultiUserViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var formatUtils = FormatUtils()

    // GPX 변수
    private val locationList = mutableListOf<Location>()
    private val heartRateList = mutableListOf<Int>()
    private val paceList = mutableListOf<Double>()
    private val cadenceList = mutableListOf<Int>()
    private var currentCadence by mutableIntStateOf(0)

    // 상태 변수들
    private var totalDistance by mutableDoubleStateOf(0.0)
    private var speed by mutableDoubleStateOf(0.0)
    private var elapsedTime by mutableLongStateOf(0L)
    private var averagePace by mutableDoubleStateOf(0.0)
    private var heartRate by mutableStateOf("---")
    private var averageHeartRate by mutableDoubleStateOf(0.0)
    private var heartRateSum = 0
    private var heartRateCount = 0

    // 발걸음수
    private var stepCount = 0  // 누적 걸음 수
    private val stepTimes = mutableListOf<Long>()  // 걸음 이벤트 발생 시간 기록

    // 절전 모드
    private lateinit var wakeLock: PowerManager.WakeLock

    private var startTime = 0L
    private var isRunning = false
    private var lastLocation: Location? = null

    private val handler = Handler(Looper.getMainLooper())

    // LaunchedEffect에서 데이터 측정 함수 실행하기 위한 변수
    private var startTrackingRequested by mutableStateOf(false)


    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                averagePace = if (totalDistance > 0) {
                    val paceInSeconds = elapsedTime / 1000.0 / totalDistance
                    paceInSeconds / 60
                } else 0.0
                // GameViewModel에서 itemUsed값 가져와서 아이템 사용했는지 체크
                val itemUsed = gameViewModel.itemUsedSignal.value
                Log.d("itemUsedCheck", "체크 : $itemUsed")
                // itemUsed 상태에 따라 데이터 전송
                if (itemUsed) {
                    sendDataToPhone(itemUsed = true)
                } else {
                    sendDataToPhone()
                }

                handler.postDelayed(this, 1000)
            }
        }
    }
    // 결과창 보여주기 위한 변수
    private var showStats by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val gameViewModel: GameViewModel by viewModels()
            val multiUserViewModel: MultiUserViewModel by viewModels()
            RunningApp(gameViewModel, multiUserViewModel) }
        // 데이터 측정 변수 상태 관찰하여 자동 실행
        observeStartTrackingState()
        // 절전모드 방지를 위한 WakeLock 초기화 및 활성화
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RunningApp::Wakelock")
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
        // 포그라운드 서비스 시작
        startForegroundService()
        // 센서 업데이트 알람 설정 추가
        scheduleSensorUpdates()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location.accuracy < 10) {
                        updateLocation(location)
                    }
                }
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepDetector", "Step detector sensor registered successfully.")
        } ?: Log.w("StepDetector", "No step detector sensor available.")

        requestPermissions()
    }


    private fun observeStartTrackingState() {
        // 상태가 true로 변경되면 `startTracking()` 실행
        handler.post(object : Runnable {
            override fun run() {
                if (startTrackingRequested && !isRunning) {
                    startTracking()
                    startTrackingRequested = false  // 실행 후 초기화
                }
                handler.postDelayed(this, 500)  // 주기적으로 상태 확인
            }
        })
    }

    // 포그라운드서비스 시작
    private fun startForegroundService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        startForegroundService(serviceIntent)  // Android 8.0 이상에서는 이 메서드 사용
    }


    override fun onResume() {
        super.onResume()
        registerHeartRateSensor()
    }

    override fun onPause() {
        super.onPause()
        if (wakeLock.isHeld) {
            Log.e("WakeLock", "WakeLock 확인")
            wakeLock.release()
        }
        sensorManager.unregisterListener(this)
    }

    // 심박수 센서 등록
    private fun registerHeartRateSensor() {
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("HeartRate", "Heart rate sensor registered successfully.")
        } ?: Log.w("HeartRate", "No heart rate sensor available.")
    }

    // 권한 요청
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BODY_SENSORS
        )

        if (permissions.any {
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    private var lastDistanceUpdate = 0.0  // 마지막으로 게이지가 업데이트된 거리

    // 위치 업데이트후 거리 계산
    private fun updateLocation(location: Location) {
        lastLocation?.let {
            val distanceMoved = it.distanceTo(location) / 1000  // 이동 거리를 km 단위로 계산
            if (distanceMoved > 0.003) {  // 이동이 미미한 경우 제외 (3m 이하)
                totalDistance += distanceMoved
                speed = location.speed * 3.6

                // 게이지 증가 로직: 1m마다 증가(나중에 7.5m마다 증가되게 수정할 예정)
                if (totalDistance - lastDistanceUpdate >= 0.01) {
                    val isFeverTime = gameViewModel.feverTimeActive.value
                    val gaugeIncrement = if (isFeverTime) 2 else 1  // 피버타임일 경우 2배로 증가
                    gameViewModel.increaseItemGauge(gaugeIncrement)
                    lastDistanceUpdate = totalDistance
                    if (gameViewModel.itemGaugeValue.value == 100) {
                      gameViewModel.handleGaugeFull(this)
               }
                }
            }
        }
        lastLocation = location
        locationList.add(location)
        paceList.add(averagePace)
    }

    // 운동 시작
    private fun startTracking() {
        if (isRunning) return
        resetTrackingData()
        isRunning = true
        startTime = System.currentTimeMillis()
        handler.post(updateRunnable)
        cadenceHandler.post(updateCadenceRunnable)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).setMinUpdateIntervalMillis(500)
            .setMinUpdateDistanceMeters(2.0f)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun resetTrackingData() {
        totalDistance = 0.0
        elapsedTime = 0L
        averagePace = 0.0
        speed = 0.0
        heartRateSum = 0
        heartRateCount = 0
        averageHeartRate = 0.0
        heartRate = "--"
        lastLocation = null
        currentCadence = 0
    }

    private fun stopTracking() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // GameViewModel에서 가져온 총 아이템 사용 횟수
        val totalItemUsage = gameViewModel.totalItemUsageCount.value

        if (heartRateCount > 0) {
            averageHeartRate = heartRateSum.toDouble() / heartRateCount
        }
        cadenceHandler.removeCallbacks(updateCadenceRunnable)

        Log.d("Stats",
            "Elapsed Time: ${formatUtils.formatTime(elapsedTime)}, Distance: $totalDistance km, Avg Pace: $averagePace min/km, Avg Heart Rate: ${"%.1f".format(averageHeartRate)} bpm")

        showStats = true
        sendFinalResultToPhone(totalItemUsage)
        createAndSendGpxFile()
    }


    // 최종 결과 보내는 함수(모바일에서 데이터 받는
    private fun sendFinalResultToPhone(totalItemUsage: Int) {
        val dataMapRequest = PutDataMapRequest.create("/final_result_data").apply {
            dataMap.putDouble("distance", totalDistance)
            dataMap.putLong("time", elapsedTime)
            dataMap.putDouble("averagePace", averagePace)
            dataMap.putDouble("averageHeartRate", averageHeartRate)
            dataMap.putInt("totalItemUsage", totalItemUsage)  // 총 아이템 사용 횟수 추가
        }.asPutDataRequest().setUrgent()

        Log.d("Final Data 전송", "총 아이템 사용 횟수: $totalItemUsage")

        Wearable.getDataClient(this).putDataItem(dataMapRequest)
            .addOnSuccessListener {
                Log.d("RunningActivity", "Final result data sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("RunningActivity", "Failed to send final result data", e)
            }
    }

    // GPX 만들고 보내는 함수
    private fun createAndSendGpxFile() {
        val gpxString = createGpxString()
        val gpxBytes = gpxString.toByteArray(Charsets.UTF_8)

        val asset = Asset.createFromBytes(gpxBytes)

        val putDataMapReq = PutDataMapRequest.create("/gpx_data").apply {
            dataMap.putAsset("gpx_file", asset)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()

        Wearable.getDataClient(this).putDataItem(putDataReq)
            .addOnSuccessListener {
                Log.d("RunningActivity", "GPX data sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("RunningActivity", "Failed to send GPX data", e)
            }
    }

    // GPx 문자열 형태로 바꾸는 함수
    private fun createGpxString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        val gpxBuilder = StringBuilder()
        gpxBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        gpxBuilder.append("<gpx version=\"1.1\" creator=\"RocatRun Wear App\">\n")
        gpxBuilder.append("  <trk>\n")
        gpxBuilder.append("    <name>RocatRun Activity</name>\n")
        gpxBuilder.append("    <trkseg>\n")

        val startTime = System.currentTimeMillis()
        var locationIndex = 0
        var lastHeartRate = 0

        for (i in heartRateList.indices) {
            val heartRate = if (i < heartRateList.size) heartRateList[i] else lastHeartRate
            lastHeartRate = heartRate
            val pace = if (i < paceList.size) paceList[i] else 0.0
            val time = startTime + (i * 1000)
            if (locationIndex < locationList.size && locationList[locationIndex].time <= time) {
                val location = locationList[locationIndex]
                gpxBuilder.append("      <trkpt lat=\"${location.latitude}\" lon=\"${location.longitude}\">\n")
                gpxBuilder.append("        <ele>${location.altitude}</ele>\n")
                locationIndex++
            } else {
                gpxBuilder.append("      <trkpt>\n")
                gpxBuilder.append("        <ele></ele>\n")
            }

            gpxBuilder.append("        <extensions>\n")
            gpxBuilder.append("          <gpxtpx:TrackPointExtension>\n")
            gpxBuilder.append("            <gpxtpx:hr>$heartRate</gpxtpx:hr>\n")
            gpxBuilder.append("            <gpxtpx:pace>$pace</gpxtpx:pace>\n")
            gpxBuilder.append("            <gpxtpx:cad>${cadenceList.lastOrNull() ?: 0}</gpxtpx:cad>\n")
            gpxBuilder.append("          </gpxtpx:TrackPointExtension>\n")
            gpxBuilder.append("        </extensions>\n")
            gpxBuilder.append("        <time>${sdf.format(Date(time))}</time>\n")
            gpxBuilder.append("      </trkpt>\n")
        }

        gpxBuilder.append("    </trkseg>\n")
        gpxBuilder.append("  </trk>\n")
        gpxBuilder.append("</gpx>")

        return gpxBuilder.toString()
    }

    @Composable
    fun RunningApp(gameViewModel: GameViewModel, multiUserViewModel: MultiUserViewModel) {
        val activity = LocalContext.current as? RunningActivity
        var isCountdownFinished by remember { mutableStateOf(false) }
        var countdownValue by remember { mutableIntStateOf(5) }

        // 카운트다운 실행
        LaunchedEffect(Unit) {
            while (countdownValue > 0) {
                delay(1000)
                countdownValue -= 1
            }
            isCountdownFinished = true

            // 카운트다운 완료 후 트래킹 상태 변경
            activity?.startTrackingRequested = true

        }

        if (isCountdownFinished) {
            WatchAppUI(gameViewModel, multiUserViewModel)
        } else {
            CountdownScreen(countdownValue)
        }
    }

    // 카운트 다운 화면
    @Composable
    fun CountdownScreen(countdownValue: Int) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = countdownValue.toString(),
                color = Color.White,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.neodgm))
            )
        }
    }

    @Composable
    fun WatchAppUI(gameViewModel: GameViewModel, multiUserViewModel: MultiUserViewModel) {
        val pagerState = rememberPagerState(pageCount = {4})
        if (showStats) {
            ShowStatsScreen(gameViewModel)
        } else {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> CircularLayout(gameViewModel)
                    1 -> ControlButtons { stopTracking() }
                    2 -> Box(modifier = Modifier.fillMaxSize()) {
                        GameScreen(gameViewModel,multiUserViewModel) // Modifier.fillMaxSize() 적용된 상태로 화면 전체에 표시
                    }
                    3 -> Box(modifier = Modifier.fillMaxSize()) {
                        MultiUserScreen(multiUserViewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun CircularLayout(gameViewModel: GameViewModel) {
        val itemGaugeValue by gameViewModel.itemGaugeValue.collectAsState()
        val bossGaugeValue by gameViewModel.bossGaugeValue.collectAsState()


        val itemProgress by animateFloatAsState(
            targetValue = itemGaugeValue.toFloat() / 100,
            animationSpec = tween(durationMillis = 500)
        )
        val bossProgress by animateFloatAsState(
            targetValue = bossGaugeValue.toFloat() / 100,
            animationSpec = tween(durationMillis = 500)
        )


        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            val spacing = maxWidth * 0.04f   // 요소 간 간격 조정
            CircularItemGauge(itemProgress = itemProgress,bossProgress = bossProgress, Modifier.size(200.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 페이스 정보
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "페이스",
                        color = Color(0xFF00FFCC),
                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.neodgm)),
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = formatUtils.formatPace(averagePace),
                        color = Color(0xFFFFFFFF),
                        fontSize = 25.sp,
//                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.neodgm)),
                    )
                }

                // 중앙 시간 정보
                Spacer(modifier = Modifier.height(spacing))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = formatUtils.formatTime(elapsedTime),
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.neodgm))
                    )
                }

                Spacer(modifier = Modifier.height(spacing * 1f))

                // 하단 거리와 심박수 정보
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 거리 정보
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "거리",
                            color = Color(0xFF36DBEB),
                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.neodgm)),
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)) {
                                    append("%.2f".format(totalDistance))
                                }
                                withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) {
                                    append("km")
                                }
                            },
                            fontFamily = FontFamily(Font(R.font.neodgm)),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    // 심박수 정보
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "심박수",
                            color = Color(0xFFF20089),
                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.neodgm)),
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)) {
                                    append(heartRate)
                                }
                                withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) {
                                    append("bpm")
                                }
                            },
                            fontFamily = FontFamily(Font(R.font.neodgm)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ControlButtons(stopTracking: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { startTracking() }) {
                Text("시작",
                    fontFamily = FontFamily(Font(R.font.neodgm)),)
            }

            Button(onClick = {
                stopTracking()
            }) {
                Text("종료",
                    fontFamily = FontFamily(Font(R.font.neodgm)),)
            }
        }
    }




    // 폰에 데이터 전송
    private fun sendDataToPhone(itemUsed: Boolean = false) {
        // 아이템 사용했을 때 데이터 보내는 경로
        if (itemUsed) {
            val itemUsedRequest = PutDataMapRequest.create("/use_item").apply {
                dataMap.putBoolean("itemUsed", true)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }.asPutDataRequest().setUrgent()
            Wearable.getDataClient(this).putDataItem(itemUsedRequest)
                .addOnSuccessListener {
                    Log.d("RunningActivity", "아이템 사용 신호 성공적으로 보냄: $itemUsed")
                }
                .addOnFailureListener { e ->
                    Log.e("RunningActivity", "아이템 사용 신호 보내지 못하였음", e)
                }
        } else {

            val dataMapRequest = PutDataMapRequest.create("/running_data").apply {
                dataMap.putDouble("pace", averagePace)
                dataMap.putDouble("distance", totalDistance)
                dataMap.putLong("time", elapsedTime)
                dataMap.putString("heartRate", heartRate)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }.asPutDataRequest()
            Log.d(
                "데이터 전송 함수",
                "데이터 형태 - Pace : $averagePace distance : $totalDistance, time : $elapsedTime, heartRate: $heartRate"
            )
            dataMapRequest.setUrgent() // 즉시 전송하도록 설정

            Wearable.getDataClient(this).putDataItem(dataMapRequest)
                .addOnSuccessListener {
                    Log.d("RunningActivity", "Data sent successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("RunningActivity", "Failed to send data", e)
                }
        }
    }
    @Composable
    fun ShowStatsScreen(gameViewModel: GameViewModel) {
        // 아이템 총 사용 횟수
        val totalItemUsageCount by gameViewModel.totalItemUsageCount.collectAsState()
        val averageCadence = calculateAverageCadence()
        val statsData = listOf(
            "총 시간: ${formatUtils.formatTime(elapsedTime)}",
            "총 거리: ${"%.2f".format(totalDistance)} km",
            "평균 페이스: ${"%.2f".format(averagePace)} min/km",
            "평균 심박수: ${"%.1f".format(averageHeartRate)} bpm",
            "평균 케이던스: $averageCadence spm",
            "총 아이템 사용 횟수 : $totalItemUsageCount"
        )

        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(statsData) { text ->
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        showStats = false
                        resetTrackingData()
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Confirm")
                }
            }
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_HEART_RATE -> {
                val newHeartRate = event.values[0].toInt()
                Log.d("심박수", "심박수: $newHeartRate")
                if (newHeartRate > 0) {
                    heartRate = newHeartRate.toString()
                    heartRateSum += newHeartRate
                    heartRateCount++
                    heartRateList.add(newHeartRate)
                    Log.d("추가", "심박수 추가")
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                stepCount++
                val currentTime = System.currentTimeMillis()
                stepTimes.add(currentTime)
                Log.d("걸음수", "걸음시간 :$stepTimes" )

                Log.d("StepTimes", "Step times: $stepTimes")
                Log.d("CadenceCalculation", "Elapsed time in minutes: ${(stepTimes.last() - stepTimes.first()) / 60000.0}")

                Log.d("StepDetector", "Step detected. Total steps: $stepCount")
                updateCadence()
            }
        }
    }

    private fun calculateCurrentCadence(): Int {
        val now = System.currentTimeMillis()
        val timeWindow = 30000 // 최근 30초 데이터 사용

        // 최근 30초 내 걸음 수 계산
        val recentSteps = stepTimes.count { it > now - timeWindow }

        // 분당 걸음 수로 변환 (recentSteps * 2)
        return recentSteps * 2
    }

    // 주기적 업데이트를 위한 핸들러
    private val cadenceHandler = Handler(Looper.getMainLooper())
    private val updateCadenceRunnable = object : Runnable {
        override fun run() {
            updateCadence()
            cadenceHandler.postDelayed(this, 5000) // 5초 간격 업데이트
        }
    }

    private fun updateCadence() {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - 60000

        // 최근 1분간 걸음 수 계산
        val recentSteps = stepTimes.count { it > oneMinuteAgo }
        currentCadence = recentSteps

        cadenceList.add(recentSteps)
    }

    // 오래된 데이터 정리
    private fun cleanOldSteps() {
        val now = System.currentTimeMillis()
        stepTimes.removeAll { it < now - 60000 } // 1분 이전 데이터 삭제
    }


    private fun calculateAverageCadence(): Int {
        if (stepTimes.size <= 1) return 0
        val elapsedTimeInMinutes = (stepTimes.last() - stepTimes.first()) / 60000.0
        return if (elapsedTimeInMinutes > 0) {
            (stepTimes.size / elapsedTimeInMinutes).roundToInt()
        } else {
            0
        }
    }

    // 알람 설정 메서드
    private fun scheduleSensorUpdates() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, SensorUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60000,  // 첫 시작 시간
            60000,  // 1분 반복 주기
            pendingIntent
        )

        Log.d("AlarmManager", "Alarm scheduled for sensor updates.")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}