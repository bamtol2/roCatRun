package com.eeos.rocatrun.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.net.URISyntaxException


object SocketHandler {
    lateinit var mSocket: Socket

    // 유저 2 생성 토큰
    private var user1Token: String = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMyIsImlhdCI6MTczOTE2NjMzMCwiZXhwIjoxNzM5MjUyNzMwfQ.ns5brW-5EdxywhV6LhOUafpSzRXEOKZ1Y7xV9pO06sXVml-GquLNOwdbmGGaJK4URF5RgjhGJy11Aghm7d7buw"

    private var authValue = "Bearer $user1Token"

    // 웹소켓 통신 베이스 주소 - 추후 https 로 바뀔 예정
    private var user1Port = "http://i12e205.p.ssafy.io:9092/"

    // 1. OkHttpClient에 헤더 인터셉터 추가
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", authValue)
                    .build()
                return chain.proceed(request)
            }
        })
        .build()

    // 2. Socket.IO 옵션 생성
    private val options = IO.Options().apply {
        callFactory = httpClient
        webSocketFactory = httpClient
        transports = arrayOf("websocket", "polling")
    }

    // 소켓 초기화
    fun initialize() {
        try {
            if (::mSocket.isInitialized && mSocket.connected()) {
                mSocket.disconnect()
            }
            mSocket = IO.socket(user1Port, options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    // 소켓 연결
    fun connect() {

        // 중복 등록 방지를 위해 기존리스너 제거
        mSocket.off(Socket.EVENT_CONNECT)

        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("Socket", "Connected!")
            authenticate() // 연결 완료 후 인증 이벤트 emit
        }

        // 인증 응답 처리
        mSocket.on("authenticated") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val json = args[0] as JSONObject
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
                put("token", user1Token)
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

