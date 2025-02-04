package com.eeos.rocatrun.service
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException

object SocketHandler {
    lateinit var socket: Socket

    // 소켓 초기화
    fun initialize() {
        try {
            // 서버 주소와 포트 변경 필요
            socket = IO.socket("http://your_server_address:port")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    // 소켓 연결
    fun connect() {
        try {
            socket.connect()
            Log.d("Connected", "OK")
        } catch (e: URISyntaxException) {
            Log.d("ERR", e.toString())
        }
        socket.on(Socket.EVENT_CONNECT, onConnect)
    }

    private val onConnect = Emitter.Listener {
        socket.emit("connectReceive", "OK")
    }

    // 소켓 끊기
    fun disconnect() {
        if (socket.connected()) {
            socket.disconnect()
        }
    }
}