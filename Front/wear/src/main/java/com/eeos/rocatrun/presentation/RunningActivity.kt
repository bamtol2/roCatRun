package com.eeos.rocatrun.presentation

import android.Manifest
import android.content.Context
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.eeos.rocatrun.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


class RunningActivity : ComponentActivity(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null

    // 상태 변수들
    private var totalDistance by mutableStateOf(0.0)
    private var speed by mutableStateOf(0.0)
    private var elapsedTime by mutableStateOf(0L)
    private var averagePace by mutableStateOf(0.0)
    private var heartRate by mutableStateOf("--")
    private var averageHeartRate by mutableStateOf(0.0)
    private var heartRateSum = 0
    private var heartRateCount = 0

    private var startTime = 0L
    private var isRunning = false
    private var lastLocation: Location? = null

    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                averagePace = if (totalDistance > 0) {
                    val paceInSeconds = elapsedTime / 1000.0 / totalDistance
                    paceInSeconds / 60
                } else 0.0

                // 데이터 전송 추가
                sendDataToPhone()

                handler.postDelayed(this, 1000)
            }
        }
    }

    private var showStats by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RunningApp() }

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

        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        registerHeartRateSensor()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun registerHeartRateSensor() {
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("HeartRate", "Heart rate sensor registered successfully.")
        } ?: Log.w("HeartRate", "No heart rate sensor available.")
    }

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

    private fun updateLocation(location: Location) {
        lastLocation?.let {
            val distanceMoved = it.distanceTo(location) / 1000
            if (distanceMoved > 0.003) {
                totalDistance += distanceMoved
                speed = location.speed * 3.6
            }
        }
        lastLocation = location
    }

    private fun startTracking() {
        if (isRunning) return
        resetTrackingData()
        isRunning = true
        startTime = System.currentTimeMillis()
        handler.post(updateRunnable)

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
    }

    private fun stopTracking() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
        fusedLocationClient.removeLocationUpdates(locationCallback)

        if (heartRateCount > 0) {
            averageHeartRate = heartRateSum.toDouble() / heartRateCount
        }

        Log.d("Stats", "Elapsed Time: ${formatTime(elapsedTime)}, Distance: $totalDistance km, Avg Pace: $averagePace min/km, Avg Heart Rate: ${"%.1f".format(averageHeartRate)} bpm")
        showStats = true
    }

    @Composable
    fun RunningApp() {
        var isCountdownFinished by remember { mutableStateOf(false) }
        var countdownValue by remember { mutableStateOf(5) }

        // 카운트다운 실행
        LaunchedEffect(Unit) {
            while (countdownValue > 0) {
                delay(1000)
                countdownValue -= 1
            }
            isCountdownFinished = true
        }

        if (isCountdownFinished) {
            WatchAppUI()
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
    fun WatchAppUI() {
        val pagerState = rememberPagerState(pageCount = {3})

        if (showStats) {
            ShowStatsScreen()
        } else {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> CircularLayout()
                    1 -> ControlButtons { stopTracking() }
                    2 -> Box(modifier = Modifier.fillMaxSize()) {
                        GameScreen() // Modifier.fillMaxSize() 적용된 상태로 화면 전체에 표시
                    }
                }
            }
        }
    }

    @Composable
    fun CircularLayout() {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            val iconSize = maxWidth * 0.12f  // 화면 크기에 따라 아이콘 크기 조정
            val spacing = maxWidth * 0.04f   // 요소 간 간격 조정

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 페이스 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.wear_icon_run),
                        contentDescription = "페이스 아이콘",
                        modifier = Modifier.size(iconSize),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(spacing))
                    Text(
                        text = "페이스\n${formatPace(averagePace)}",
                        color = Color(0xFF00FFFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.neodgm)),

                    )
                }

                // 중앙 시간 정보
                Spacer(modifier = Modifier.height(spacing))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.wear_icon_time),
                        contentDescription = "시간 아이콘",
                        modifier = Modifier.size(iconSize * 1.5f),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(spacing))
                    Text(
                        text = formatTime(elapsedTime),
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.neodgm))
                    )
                }

                Spacer(modifier = Modifier.height(spacing * 1.5f))

                // 하단 거리와 심박수 정보
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 거리 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.wear_icon_distance),
                            contentDescription = "거리 아이콘",
                            modifier = Modifier.size(iconSize),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(spacing))
                        Text(
                            text = "거리\n${"%.2f".format(totalDistance)} km",
                            color = Color(0xFF00FF00),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.neodgm)),
                            textAlign = TextAlign.Center
                        )
                    }

                    // 심박수 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.wear_icon_heart),
                            contentDescription = "심박수 아이콘",
                            modifier = Modifier.size(iconSize),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(spacing))
                        Text(
                            text = "심박수\n$heartRate bpm",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.neodgm)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ControlButtons(onStopTracking: () -> Unit) {
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
                onStopTracking()
            }) {
                Text("종료",
                    fontFamily = FontFamily(Font(R.font.neodgm)),)
            }
        }
    }

    // 폰에 데이터 전송
    private fun sendDataToPhone() {
        val dataMapRequest = PutDataMapRequest.create("/running_data").apply {
            dataMap.putDouble("pace", averagePace)
            dataMap.putDouble("distance", totalDistance)
            dataMap.putLong("time", elapsedTime)
            dataMap.putString("heartRate", heartRate)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest()

        dataMapRequest.setUrgent() // 즉시 전송하도록 설정

        Wearable.getDataClient(this).putDataItem(dataMapRequest)
            .addOnSuccessListener {
                Log.d("RunningActivity", "Data sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("RunningActivity", "Failed to send data", e)
            }
    }

    @Composable
    fun ShowStatsScreen() {
        val statsData = listOf(
            "Total Time: ${formatTime(elapsedTime)}",
            "Total Distance: ${"%.2f".format(totalDistance)} km",
            "Average Pace: ${"%.2f".format(averagePace)} min/km",
            "Average Heart Rate: ${"%.1f".format(averageHeartRate)} bpm"
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

    private fun formatPace(pace: Double): String {
        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).roundToInt()
        return String.format("%d'%02d\"", minutes, seconds)
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60))

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val newHeartRate = event.values[0].toInt()
            if (newHeartRate > 0) {
                heartRate = newHeartRate.toString()
                heartRateSum += newHeartRate
                heartRateCount++
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}