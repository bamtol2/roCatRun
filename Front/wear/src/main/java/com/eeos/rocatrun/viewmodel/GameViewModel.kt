package com.eeos.rocatrun.viewmodel

import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.media.MediaPlayer
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eeos.rocatrun.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.eeos.rocatrun.presentation.ResultActivity

/**
 * 게임의 상태 관리하는 ViewModel 클래스
 * 아이템게이지, 보스 게이지, 피버 타임 상태 값 관리
 */


class GameViewModel : ViewModel() {
    // 중복 실행 방지 플래그
    private var isHandlingGauge = false

    private val _itemGaugeValue = MutableStateFlow(0)
    private val _bossGaugeValue = MutableStateFlow(100)
    private val _feverTimeActive = MutableStateFlow(false)
    private val _showItemGif = MutableStateFlow(false)
    private val _itemUsageCount = MutableStateFlow(0)

    // 아이템 사용 신호 변수
    private var _itemUsedSignal = MutableStateFlow(false)

    // 총 아이템 사용 횟수
    private var _totalItemUsageCount = MutableStateFlow(0)

    // 외부에서 읽기 전용으로 사용할 수 있는 상태 흐름 (StateFlow).
    val itemGaugeValue = _itemGaugeValue.asStateFlow()
    val bossGaugeValue = _bossGaugeValue.asStateFlow()
    val feverTimeActive = _feverTimeActive.asStateFlow()
    val showItemGif = _showItemGif.asStateFlow()
    val itemUsedSignal = _itemUsedSignal.asStateFlow()
    val totalItemUsageCount = _totalItemUsageCount.asStateFlow()

    // 총 아이템 사용 횟수 증가 함수
    private fun incrementTotalItemUsageCount() {
        _totalItemUsageCount.value++
        Log.d("GameViewModel", "총 아이템 사용 횟수 증가: ${_totalItemUsageCount.value}")
    }
    // 총 아이템 사용 횟수 초기화 함수
    fun resetTotalItemUsageCount() {
        _totalItemUsageCount.value = 0
        Log.d("GameViewModel", "총 아이템 사용 횟수 초기화")
    }

    // 아이템 사용 시 호출하는함수
    fun notifyItemUsage(){
        _itemUsedSignal.value = true
        incrementTotalItemUsageCount()
        viewModelScope.launch {
            delay(1000) // 중복 전송 방지하기 위한 딜레이
            _itemUsedSignal.value = false
        }
    }

    // 아이템 게이지 증가
    fun increaseItemGauge(amount : Int) {
        // 현재 게이지 값에 양을 추가하며, 최대 100을 넘지 않도록 제한
        _itemGaugeValue.value = (_itemGaugeValue.value + amount).coerceAtMost(100)
        Log.d("아이템 게이지", "현재 게이지 값: ${_itemGaugeValue.value}, 증가량: $amount")
//        Log.i("아이템 사용 횟수 ", "횟수 : ${_itemUsageCount.value}")
    }

    /**
     * 아이템 게이지가 가득 찼을 때 호출되는 함수.
     * - 아이템 사용 애니메이션을 보여주고 보스 게이지를 감소시킴.
     * - 두 번 사용 시 피버 타임을 시작함.
     */
    fun handleGaugeFull(context: Context) {
        if (isHandlingGauge) return

        isHandlingGauge = true
        viewModelScope.launch {
            notifyItemUsage()
            _itemUsageCount.value++
            _showItemGif.value = true
            delay(1000)
            _showItemGif.value = false

            _itemGaugeValue.value = 0
            _bossGaugeValue.value = (_bossGaugeValue.value - 20).coerceAtLeast(0)

//            if (_bossGaugeValue.value == 0) {
//                stopFeverTimeEffects()  // 효과 중지
//                navigateToResultActivity(context)  // 결과 액티비티로 이동
//                return@launch
//            }


            isHandlingGauge = false
        }
    }

    // 피버타임 시작
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun startFeverTime(context: Context) {
        _feverTimeActive.value = true

        // 진동과 소리 재생
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createWaveform(longArrayOf(3000, 2000), intArrayOf(100, 0), 0)
        vibrator?.vibrate(vibrationEffect)

        mediaPlayer = MediaPlayer.create(context, R.raw.fever_time_sound).apply {
            start()
        }

        viewModelScope.launch {
            delay(30000)
            stopFeverTimeEffects()
            _itemUsageCount.value = 0
        }
    }

    // 피버타임 효과 중지
    fun stopFeverTimeEffects() {
        _feverTimeActive.value = false
        vibrator?.cancel()
        vibrator = null

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        mediaPlayer = null
    }

    // 결과 창으로 가는 함수
//    private fun navigateToResultActivity(context: Context) {
//        resetTotalItemUsageCount()
//        val intent = Intent(context, ResultActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        context.startActivity(intent)
//    }

    fun observeFeverEvents(viewModel: MultiUserViewModel, context: Context) {
        viewModelScope.launch {
            viewModel.feverEventFlow.collect { isFeverStart ->
                if (isFeverStart) {
                    startFeverTime(context)  // 피버 타임 시작
                } else {
                    stopFeverTimeEffects()  // 피버 타임 종료
                }
            }
        }
    }
}
