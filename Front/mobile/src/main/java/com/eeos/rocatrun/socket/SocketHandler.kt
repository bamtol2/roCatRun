package com.eeos.rocatrun.socket

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.net.URISyntaxException
import com.eeos.rocatrun.login.data.TokenStorage
import com.google.android.play.integrity.internal.l

object SocketHandler {

    lateinit var mSocket: Socket

    // Context로부터 토큰을 받아올 때까지 null로 초기화
    private var userToken: String = ""
    private var authValue: String = ""

    // 웹소켓 통신 베이스 주소
    private var userPort = "http://i12e205.p.ssafy.io:9092/"

    private lateinit var httpClient: OkHttpClient

    // 외부에서 context를 전달받아 초기화하도록 변경
    fun initialize(context: Context) {
        // TokenStorage에서 토큰을 읽어옴
        userToken = TokenStorage.getAccessToken(context) ?: ""
        authValue = "Bearer $userToken"

        // OkHttpClient 생성 (인터셉터에 authValue 적용)
        httpClient = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Authorization", authValue)
                        .build()
                    return chain.proceed(request)
                }
            })
            .build()

        // Socket.IO 옵션 생성
        val options = IO.Options().apply {
            callFactory = httpClient
            webSocketFactory = httpClient
            transports = arrayOf("websocket", "polling")
        }

        // 소켓 초기화
        try {
            if (::mSocket.isInitialized && mSocket.connected()) {
                mSocket.disconnect()
            }
            mSocket = IO.socket(userPort, options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    // 소켓 연결
    fun connect() {

        // 중복 등록 방지를 위해 기존 리스너 제거
        mSocket.off(Socket.EVENT_CONNECT)

        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("Socket", "Connected!")
            authenticate() // 연결 완료 후 인증 이벤트 emit
        }

        // 인증 응답 처리
        mSocket.on("authenticated") { args ->
            if (args.isNotEmpty() && args is JSONObject) {
                val json = args as JSONObject
                val success = json.optBoolean("success", false)
                Log.d("Socket", "On - authenticated : success=$success")
            }
        }

        // 연결 시작
        mSocket.connect()
    }

    // 토큰 인증
    fun authenticate() {
        try {
            Log.d("Socket", "Emit - authenticate")
            mSocket.emit("authenticate", JSONObject().apply {
                put("token", userToken)
            })
        } catch (e: URISyntaxException) {
            Log.d("Socket", e.toString())
        }
    }

    // 소켓 끊기
    fun disconnect() {
        if (mSocket.connected()) {
            mSocket.disconnect()
        }
    }


}

