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
import com.eeos.rocatrun.presentation.ItemActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import android.speech.tts.TextToSpeech

/**
 * 게임의 상태 관리하는 ViewModel 클래스
 * 아이템게이지, 보스 게이지, 피버 타임 상태 값 관리
 */


class GameViewModel : ViewModel() {
    // 중복 실행 방지 플래그
    private var isHandlingGauge = false

    private val _itemGaugeValue = MutableStateFlow(0)
    // BossHealthRepository에서 관리하는 값을 가져옴
    private val _bossGaugeValue = MutableStateFlow(0)

    private val _feverTimeActive = MutableStateFlow(false)
    private val _showItemGif = MutableStateFlow(false)
    private val _itemUsageCount = MutableStateFlow(0)

    // 아이템 사용 신호 변수
    private var _itemUsedSignal = MutableStateFlow(false)

    // 총 아이템 사용 횟수
    private var _totalItemUsageCount = MutableStateFlow(0)

    // 사용가능한 아이템 횟수
    private var _avaliableItemCount = MutableStateFlow(0)

    // 외부에서 읽기 전용으로 사용할 수 있는 상태 흐름 (StateFlow).
    val itemGaugeValue: StateFlow<Int> get() = _itemGaugeValue
    val bossGaugeValue: StateFlow<Int> get() = _bossGaugeValue
    val feverTimeActive: StateFlow<Boolean> get() = _feverTimeActive
    val showItemGif = _showItemGif.asStateFlow()
    val itemUsedSignal = _itemUsedSignal.asStateFlow()
    val totalItemUsageCount = _totalItemUsageCount.asStateFlow()
    val availableItemCount = _avaliableItemCount.asStateFlow()




    // GameViewModel 초기화 시 BossHealthRepository의 bossHealth를 구독하여 보스 게이지 업데이트
    init {
        viewModelScope.launch {
            BossHealthRepository.bossHealth.collect { health ->
                _bossGaugeValue.value = health
                Log.d("GameViewModel", "bossGaugeValue updated to $health")
            }
        }
    }
    // 총 아이템 사용 횟수 증가 함수
    private fun incrementTotalItemUsageCount() {
        _totalItemUsageCount.value++
        Log.d("GameViewModel", "총 아이템 사용 횟수 증가: ${_totalItemUsageCount.value}")
    }


    // 아이템 사용 시 호출하는함수
    fun notifyItemUsage(){
        if (_avaliableItemCount.value > 0){
            _itemUsedSignal.value = true
            _showItemGif.value = true

            incrementTotalItemUsageCount()
            viewModelScope.launch {
                delay(500) // 중복 전송 방지하기 위한 딜레이
                _avaliableItemCount.value = maxOf(_avaliableItemCount.value - 1, 0)
                _itemUsedSignal.value = false
                _showItemGif.value = false
            }
            Log.d("GameViewModel", "아이템 사용")
        }else{
            Log.d("GameViewModel", "사용 가능한 아이템이 없습니다.")
        }

    }

    fun setItemGauge(value: Int) {
        _itemGaugeValue.value = value.coerceIn(0, 100)
        Log.d("GameViewModel", "Item gauge set to ${_itemGaugeValue.value}")
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
//            notifyItemUsage()
            _avaliableItemCount.value++
            _itemUsageCount.value++
//            _showItemGif.value = true
            delay(500)
//            _showItemGif.value = false
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val vibrationEffect = VibrationEffect.createWaveform(longArrayOf(1000), intArrayOf(100), -1)
            vibrator?.vibrate(vibrationEffect)
//            var tts: TextToSpeech? = null
//            tts = TextToSpeech(context) { status ->
//                if (status == TextToSpeech.SUCCESS) {
//                    val result = tts?.setLanguage(java.util.Locale.KOREAN)
//                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Log.e("TTS", "한국어 TTS를 지원하지 않습니다.")
//                    } else {
//                        // 사용 가능한 아이템 개수를 알림
//                        val text = "사용 가능한 아이템 개수는 ${_avaliableItemCount.value}개 입니다."
//                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//                    }
//                } else {
//                    Log.e("TTS", "TTS 초기화 실패")
//                }
//            }
//            delay(1000)
//            tts.shutdown()

            _itemGaugeValue.value = 0
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

//        mediaPlayer = MediaPlayer.create(context, R.raw.fever_time_sound).apply {
//            start()
//        }

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
