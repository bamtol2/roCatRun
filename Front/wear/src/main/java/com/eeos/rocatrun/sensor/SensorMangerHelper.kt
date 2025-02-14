package com.eeos.rocatrun.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SensorManagerHelper(
    private val context: Context,
    private val onHeartRateUpdated: (Int) -> Unit,
    private val onStepDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var heartRateSensor: Sensor? = null
    private var stepDetector: Sensor? = null

    fun registerSensors() {
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SensorManagerHelper", "Heart rate sensor registered.")
        } ?: Log.w("SensorManagerHelper", "No heart rate sensor available.")

        stepDetector?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SensorManagerHelper", "Step detector sensor registered.")
        } ?: Log.w("SensorManagerHelper", "No step detector sensor available.")
    }

    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
        Log.d("SensorManagerHelper", "Sensors unregistered.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_HEART_RATE -> {
                val heartRate = event.values[0].toInt()
                if (heartRate > 0) {
                    Log.d("SensorManagerHelper", "Heart rate: $heartRate bpm")
                    onHeartRateUpdated(heartRate)
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                Log.d("SensorManagerHelper", "Step detected.")
                onStepDetected()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
