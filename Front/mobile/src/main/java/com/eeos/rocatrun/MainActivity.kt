package com.eeos.rocatrun


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import io.socket.emitter.Emitter
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 웹소켓 연결 - 추후 로그인 토큰 연동 해주기
        // 이벤트 리스너 등록
//        SocketHandler.socket.on("authenticated", authenticatedListener)
//        SocketHandler.socket.on("roomCreated", roomCreatedListener)
//        SocketHandler.socket.on("playerJoined", playerJoinedListener)

        // 소켓 초기화 및 연결
        SocketHandler.initialize()
        SocketHandler.connect()

        // 인증 토큰 발급
//        SocketHandler.authenticate()

        enableEdgeToEdge()
        setContent {
            RoCatRunTheme {
                // 로그인 여부 확인
                // 로그인이 안되어 있다면 LoginActivity로 이동

                // 로그인이 되어 있다면 HomeActivity로 이동
            }
        }
    }
}

// 방 생성 리스너
private val roomCreatedListener = Emitter.Listener { args ->
    val data = args.getOrNull(0) as? JSONObject ?: return@Listener
    val roomId = data.optString("roomId")
    val inviteCode = data.optString("inviteCode")
    val currentPlayers = data.optInt("currentPlayers")
    val maxPlayers = data.optInt("maxPlayers")

    // 로그 출력
    Log.d("Socket", "data = $data")
}

// 플레이어 방 접속 확인 리스너
private val playerJoinedListener = Emitter.Listener { args ->
    val data = args.getOrNull(0) as? JSONObject ?: return@Listener
    val userId = data.optString("userId")
    val currentPlayers = data.optInt("currentPlayers")
    val maxPlayers = data.optInt("maxPlayers")

    Log.d("Socket", "data = $data")

}

// 토큰 확인 리스너
val authenticatedListener = Emitter.Listener { args ->
    val data = args.getOrNull(0) as? JSONObject ?: return@Listener
//    val success = data.optBoolean("success", false)

    Log.d("Socket", "data = $data")
}