package com.eeos.rocatrun.detector

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import kotlin.math.abs

/**
 * ArmGestureDetector는 accelerometer 센서의 y축과 z축 값을 이용하여
 * 팔을 위로 올리는 동작과 내리는 동작을 감지합니다.
 *
 * - onArmRaise: 팔이 위로 올려졌을 때 호출되는 콜백
 * - onArmLower: 팔이 내려졌을 때 호출되는 콜백
 *
 * 임계치와 최소 간격은 테스트 후에 조정이 필요합니다.
 */
class ArmRaiseDetector(
    private val onArmRaise: () -> Unit,
    private val onArmLower: () -> Unit
) : SensorEventListener {
    // 임계치 값 (예시 값, 실제로는 기기에 맞게 조정)
    private val ARM_RAISE_Y_THRESHOLD = 8.0f     // 팔 올릴 때 y축 임계치
    private val ARM_LOWER_Y_THRESHOLD = 4.0f      // 팔 내릴 때 y축 임계치
    private val ARM_LOWER_Z_THRESHOLD = 7.0f      // 팔 내릴 때 z축 임계치 (예: 손목이 기기의 평면과 수직에 가까워짐)

    // 최소 감지 간격 (밀리초)
    private val MIN_INTERVAL_MS = 1000L

    // 현재 동작 상태 (IDLE: 기본, RAISED: 팔 올림 상태)
    private enum class State { IDLE, ARM_RAISED }
    private var state = State.IDLE

    private var lastDetectionTime = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val y = event.values[1]  // 보통 y축은 수직 방향 (기기 착용 방식에 따라 다름)
        val z = event.values[2]  // z축 값도 기기의 회전에 따라 변할 수 있음
        val now = System.currentTimeMillis()

        when (state) {
            State.IDLE -> {
                // 팔 올리기 감지: y값이 임계치 이상이고, z값이 낮은 상태
                if (y > ARM_RAISE_Y_THRESHOLD && z < 2.0f) { // z < 2.0f 는 예시 값입니다.
                    if (now - lastDetectionTime > MIN_INTERVAL_MS) {
                        state = State.ARM_RAISED
                        lastDetectionTime = now
                        Log.d("ArmGestureDetector", "Arm raised detected: y=$y, z=$z")
                        onArmRaise()
                    }
                }
            }
            State.ARM_RAISED -> {
                // 팔 내리기 감지: y값이 낮아지고, z값이 일정 이상으로 회복됨
                if (y < ARM_LOWER_Y_THRESHOLD && z > ARM_LOWER_Z_THRESHOLD) {
                    if (now - lastDetectionTime > MIN_INTERVAL_MS) {
                        state = State.IDLE
                        lastDetectionTime = now
                        Log.d("ArmGestureDetector", "Arm lowered detected: y=$y, z=$z")
                        onArmLower()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 필요 시 센서 정확도 변경에 따른 처리
    }
}
