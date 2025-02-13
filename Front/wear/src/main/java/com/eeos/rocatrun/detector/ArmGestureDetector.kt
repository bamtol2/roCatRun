package com.eeos.rocatrun.detector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlin.math.abs
import kotlin.math.pow

/**
 * ArmGestureDetector는 accelerometer와 barometer 센서를 이용해
 * 팔을 위로 올렸다 내리는 동작(휘두르기)을 감지하는 클래스입니다.
 *
 * - onArmSwing: 팔을 위로 올리고(감지) 바로 내렸을 때 호출되는 콜백
 * - logCallback: 센서 관련 로그 메시지를 전달하는 선택적 콜백 (UI 업데이트 등에 사용 가능)
 *
 * start()를 호출하면 센서 리스너를 등록하고, stop()을 호출하면 해제합니다.
 */
class ArmGestureDetector(
    private val context: Context,
    private val onArmSwing: () -> Unit,
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var barometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    // 캘리브레이션 관련 (여기서는 기준 고도를 고정함)
    private var isCalibrating = true
    private var initialAltitude = 0.85f
    private val calibrationSamples = mutableListOf<Float>()

    // 중력 제거를 위한 변수 (저역통과 필터)
    private val gravity = FloatArray(3)
    private val linearAcceleration = FloatArray(3)

    // 동작 감지 관련 변수
    private var isArmRaised = false
    private var lastMotionTime = 0L
    private val motionThreshold = 1000L // 1초 이하의 동작이면 감지

    private val handler = Handler(Looper.getMainLooper())

    /**
     * 센서 리스너 등록 및 캘리브레이션 시작.
     * 캘리브레이션은 5초 후에 종료되며, 기준 고도는 0.85f로 고정합니다.
     */
    fun start() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        barometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }

        // 캘리브레이션 시작
        isCalibrating = true
        handler.postDelayed({
            isCalibrating = false
            // 만약 평균값을 사용하려면 calibrationSamples.average()로 계산
            // initialAltitude = if (calibrationSamples.isNotEmpty()) calibrationSamples.average().toFloat() else initialAltitude
        }, 5000)
    }

    /**
     * 센서 리스너 해제.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event.values)
            Sensor.TYPE_PRESSURE -> handleBarometer(event.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 필요 시 센서 정확도 변경 처리
    }

    private fun handleAccelerometer(values: FloatArray) {
        val alpha = 0.8f
        for (i in values.indices) {
            gravity[i] = alpha * gravity[i] + (1 - alpha) * values[i]
            linearAcceleration[i] = values[i] - gravity[i]
        }

        // 여기서는 y축 값만 사용해 간단히 동작을 감지합니다.
        val y = linearAcceleration[1]
        detectArmMovement(y)
    }

    private fun detectArmMovement(y: Float) {
        val currentTime = System.currentTimeMillis()

        // 팔을 올리는 동작 감지: y값이 임계치(예: 15) 이상이면 팔 올림으로 판단
        if (y > 15 && !isArmRaised) {
            isArmRaised = true
            lastMotionTime = currentTime
        }

        // 팔을 내리는 동작 감지: y값이 -15 이하이면 팔 내림으로 판단
        if (y < -15 && isArmRaised) {
            isArmRaised = false
            if (currentTime - lastMotionTime <= motionThreshold) {
                onArmSwing()
            }
        }
    }

    private fun handleBarometer(pressure: Float) {
        val altitude = calculateAltitude(pressure)
        if (isCalibrating) {
            calibrationSamples.add(altitude)
        }
        val altitudeChangeThreshold = 0.8f
        val altitudeChange = altitude - initialAltitude
        if (abs(altitudeChange) > altitudeChangeThreshold) {
            initialAltitude = altitude
        }
    }

    private fun calculateAltitude(pressure: Float): Float {
        val seaLevelPressure = 1013.25f
        return ((1 - (pressure / seaLevelPressure).toDouble().pow(0.190284)) * 44307.69).toFloat()
    }

}
