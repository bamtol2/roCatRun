package com.eeos.rocatrun.service

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
        // 알림을 생성하여 포그라운드 서비스로 등록
        return Notification.Builder(this, "location_channel")
            .setContentTitle("Running App")
            .setContentText("Tracking your location and heart rate.")
            .build()
    }
}