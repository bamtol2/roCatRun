package com.eeos.rocatrun.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException


object SocketHandler {
    private lateinit var mSocket: Socket

    // 유저 1 생성 토큰
    private var user1Token: String = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTczODc1NjEyOSwiZXhwIjoxNzM4ODQyNTI5fQ.Sy5H8BL2paTxiLlWT46zlMBVjyTjV2oCnI2s-4xd9Cm3kYTVjK3uXCVK-packchDPcQF0Ob0aK_DvuPNY2OejQ"

    // 웹소켓 통신 베이스 주소
    private var user1Port = "http://i12e205.p.ssafy.io:8081"

    // 소켓 초기화
    fun initialize() {
        try {
            // 헤더 설정 필요
            mSocket = IO.socket(user1Port)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    // 소켓 연결
    fun connect() {
        try {
            mSocket.connect()
            Log.d("Socket", "Connected")
        } catch (e: URISyntaxException) {
            Log.d("ERR", e.toString())
        }
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
    }

    private val onConnect = Emitter.Listener {
        mSocket.emit("connectReceive", "OK")
    }

    // 토큰 확인
    fun authenticate() {

        // token JSON 생성
        val tokenJson = JSONObject().apply {
            put("token", user1Token)
        }
        mSocket.emit("authenticate", tokenJson)
        Log.d("Socket", "authenticated")
    }

    // 소켓 끊기
    fun disconnect() {
        if (mSocket.connected()) {
            mSocket.disconnect()
        }
    }

}

