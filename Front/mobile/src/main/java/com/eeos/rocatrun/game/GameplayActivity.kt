package com.eeos.rocatrun.game

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.eeos.rocatrun.service.GamePlayService
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.PutDataMapRequest
import java.io.File
import java.util.ArrayList

class GamePlay : ComponentActivity(){

    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var dataClient: DataClient

    // 실시간 러닝 데이터
    data class RunningData(
        val distance: Double,
        val time: Long
    )

    // 실시간 팀원들 데이터
    data class PlayersData(
        val nickName: String,
        val totalDistance: Double,
        val totalItemUsage: Int,
    )

    // 워치에서 받아오는 본인 결과 데이터
    data class ResultData(
        val distance: Double,
        val time: Long,
        val averagePace: Double,
        val averageHeartRate: Double,
        val averageCadence: Double
//        val totalItemUsage: Int,
    )

    // 웹소켓에서 받아오는 나의 게임 결과 데이터
    data class MyResultData(
        val userId: String,
        val nickName: String,
        val characterImage: String,  // 유저의 프로필 이미지 파일 경로
        val runningTime: Long,
        val totalDistance: Double,
        val paceAvg: Double,
        val heartRateAvg: Double,
        val cadenceAvg: Double,
        val calories: Int,
        val itemUseCount: Int,
        val rewardExp: Int,
        val rewardCoin: Int
    )

    // 유저들 게임 결과 데이터
    data class PlayersResultData(
        val userId: String,
        val nickname: String,
        val characterImage: String,  // 유저의 프로필 이미지 파일 경로
        val totalDistance: Double,
        val itemUseCount: Int,
        val rewardExp: Int,
        val rewardCoin: Int
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 계속 켜짐 설정 추가
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // WakeLock 설정
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "RoCatRun::GamePlayWakeLock"
        )
        wakeLock.acquire(3 * 60 * 60 * 1000L) // 3시간 동안 WakeLock 유지

        // 포그라운드 서비스 시작
        startService(Intent(this, GamePlayService::class.java))

        // android 13 이상에서는 post_notifications 권한 런타임에 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        // intent로 전달된 bossHealth 추출
        val firstBossHealth = intent.getIntExtra("firstBossHealth", 100000)
        val playerNicknames = intent.getStringArrayListExtra("playerNicknames")
        val time = intent.getIntExtra("time", 1805)

        dataClient = Wearable.getDataClient(this)

        Log.d("Socket", "페이지 이동 후 표출 $firstBossHealth $playerNicknames $time")

        // 워치 앱을 시작하는 함수
        fun startWatchApp(context: Context) {
            val messageClient: MessageClient = Wearable.getMessageClient(context)
            val path = "/start_watch_app"
            val messageData = "Start Game".toByteArray()

            Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    val nodeId = nodes.first().id
                    Log.d("WearApp", "연결된 노드: ${nodes.first().displayName}")

                    messageClient.sendMessage(nodeId, path, messageData).apply {
                        addOnSuccessListener {
                            Log.d("Wear APP", "메시지 전송 성공")
                            Toast.makeText(context, "워치 앱 시작 요청 전송 완료", Toast.LENGTH_SHORT).show()

                            // 게임 초기 데이터 전송
                            if (playerNicknames != null) {
                                gameStartEvent(firstBossHealth, playerNicknames, time)
                            }
                        }
                        addOnFailureListener { exception ->
                            Log.e("Wear APP", "메시지 전송 실패: ${exception.message}")
                            Toast.makeText(context, "워치 앱 시작 요청 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d("WearApp", "연결된 노드가 없습니다.")
                    Toast.makeText(context, "연결된 디바이스가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.e("WearApp", "노드 검색 실패: ${exception.message}")
                Toast.makeText(context, "워치 연결 확인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        // 메세지 보내기
        startWatchApp(this)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )
        )

        setContent {
            RoCatRunTheme(
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameplayScreen(onShareClick = { shareLatestGpxFile() })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // WakeLock 해제
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        // 화면 켜짐 플래그 해제
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 포그라운드 서비스 중지
        stopService(Intent(this, GamePlayService::class.java))
    }

    // 워치 - 초기 boss health, 플레이어 닉네임 보내기
    private fun gameStartEvent(firstBossHealth: Int, playerNicknames: ArrayList<String>, time:Int){

        // 워치에 초기 boss health 보내기
        val putDataMapRequest = PutDataMapRequest.create("/first_boss_health")
        putDataMapRequest.dataMap.apply {
            putInt("firstBossHealth",firstBossHealth)     // 초기 보스체력
            putStringArrayList("playerNicknames", playerNicknames)    // 플레이어 닉네임
            putInt("time",time)
        }
        val request = putDataMapRequest.asPutDataRequest().setUrgent()
        dataClient.putDataItem(request)
            .addOnSuccessListener { _ ->
                Log.d("Wear", "보스 초기 체력 송신")
            }
            .addOnFailureListener { exception ->
                Log.e("Wear", "보스 초기 체력 송신 실패", exception)
            }
    }

    private fun shareLatestGpxFile() {
        val directory = getExternalFilesDir(null)
        val gpxFiles = directory?.listFiles { file -> file.name.endsWith(".gpx") }

        gpxFiles?.maxByOrNull { it.lastModified() }?.let { latestFile ->
            shareGpxFile(this, latestFile.name)
        } ?: run {
            // GPX 파일이 없을 경우 처리
            Log.e("GameMulti", "No GPX files found to share")
        }
    }

    private fun shareGpxFile(context: Context, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/gpx+xml"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "GPX 파일 공유"))
    }

}

