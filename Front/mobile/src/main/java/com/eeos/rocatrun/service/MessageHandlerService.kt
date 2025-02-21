package com.eeos.rocatrun.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eeos.rocatrun.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MessageHandlerService : WearableListenerService() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "game_channel_id"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MessageHandlerService", "서비스가 시작되었습니다.")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("MessageHandlerService", "메시지 수신 시도: ${messageEvent.path}")

        if (messageEvent.path == "/start_mobile_app") {
            val message = String(messageEvent.data)
            Log.d("MessageHandlerService", "메시지 수신 성공: $message")

            // 포그라운드 서비스 시작 및 MainActivity 실행
            startForegroundServiceWithNotification()
        } else {
            Log.d("MessageHandlerService", "경로 불일치: ${messageEvent.path}")
        }
    }

    private fun startForegroundServiceWithNotification() {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("게임 시작 알림")
            .setContentText("게임이 시작됩니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        Log.d("MessageHandlerService", "포그라운드 서비스 시작")
        startForeground(NOTIFICATION_ID, notification)

        // MainActivity 실행
        startGameActivity()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "게임 알림 채널",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "게임 시작을 알리는 채널입니다."
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("MessageHandlerService", "Notification 채널 생성 완료")
        }
    }

    private fun startGameActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        Log.d("MessageHandlerService", "MainActivity 실행 시도")
        startActivity(intent)
    }
}
