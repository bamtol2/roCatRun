package com.eeos.rocatrun.viewmodel

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 게임의 상태 관리하는 ViewModel 클래스
 * 아이템게이지, 보스 게이지, 피버 타임 상태 값 관리
 */


class GameViewModel : ViewModel() {

    private val _itemGaugeValue = MutableStateFlow(0)
    private val _bossGaugeValue = MutableStateFlow(100)
    private val _feverTimeActive = MutableStateFlow(false)
    private val _showItemGif = MutableStateFlow(false)
    private val _itemUsageCount = MutableStateFlow(0)

    // 외부에서 읽기 전용으로 사용할 수 있는 상태 흐름 (StateFlow).
    val itemGaugeValue = _itemGaugeValue.asStateFlow()
    val bossGaugeValue = _bossGaugeValue.asStateFlow()
    val feverTimeActive = _feverTimeActive.asStateFlow()
    val showItemGif = _showItemGif.asStateFlow()


    // 아이템 게이지 증가
    fun increaseItemGauge() {
        _itemGaugeValue.value = (_itemGaugeValue.value + 20).coerceAtMost(100)
    }

    /**
     * 아이템 게이지가 가득 찼을 때 호출되는 함수.
     * - 아이템 사용 애니메이션을 보여주고 보스 게이지를 감소시킴.
     * - 두 번 사용 시 피버 타임을 시작함.
     */
    fun handleGaugeFull(context: Context) {
        viewModelScope.launch {
            _itemUsageCount.value++
            _showItemGif.value = true
            delay(1000)
            _showItemGif.value = false

            _itemGaugeValue.value = 0
            _bossGaugeValue.value = (_bossGaugeValue.value - 20).coerceAtLeast(0)


            if (_itemUsageCount.value == 2) {
                startFeverTime(context)
            }
        }
    }

    /**
     * 피버 타임을 시작하는 함수.
     * - 5초 동안 피버 타임이 유지됨.
     * - 진동 효과를 트리거함.
     */
    private fun startFeverTime(context: Context) {
        _feverTimeActive.value = true
        triggerVibration(context)

        viewModelScope.launch {
            delay(5000)
            _feverTimeActive.value = false
            _itemUsageCount.value = 0
        }
    }

    private fun triggerVibration(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val timings = longArrayOf(200, 300, 200, 300, 200, 300)
        val amplitudes = intArrayOf(100, 100, 100, 100, 100, 100)
        if (vibrator.hasVibrator()) {
            val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(vibrationEffect)
        }
    }
}
