package com.eeos.rocatrun.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.SensorEventListener
import android.util.Log

class SensorUpdateReceiver : BroadcastReceiver(), SensorEventListener {

    private lateinit var sensorManager: android.hardware.SensorManager
    private var heartRateSensor: android.hardware.Sensor? = null

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("SensorUpdateReceiver", "Alarm triggered! Updating sensor data...")

        // 절전모드일때 센서 매니저 초기화 및 리스너 등록
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as android.hardware.SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_HEART_RATE)

        heartRateSensor?.let {
            sensorManager.registerListener(this, it, android.hardware.SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SensorUpdateReceiver", "Heart rate sensor registered.")
        } ?: Log.w("SensorUpdateReceiver", "Heart rate sensor not available.")
    }

    override fun onSensorChanged(event: android.hardware.SensorEvent?) {
        if (event?.sensor?.type == android.hardware.Sensor.TYPE_HEART_RATE) {
            val heartRate = event.values[0].toInt()
            Log.d("SensorUpdateReceiver", "Heart rate: $heartRate")
        }
    }

    override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
}
