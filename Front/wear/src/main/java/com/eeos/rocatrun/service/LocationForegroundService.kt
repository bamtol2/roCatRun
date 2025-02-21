package com.eeos.rocatrun.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class LocationForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Foreground Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 알림 생성 및 서비스 시작
        startForeground(1, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        return Notification.Builder(this, "location_channel")
            .setContentTitle("Running App")
            .setContentText("Tracking your location and heart rate.")
            .build()
    }
}