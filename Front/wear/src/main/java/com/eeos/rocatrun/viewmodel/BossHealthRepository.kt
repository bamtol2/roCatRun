package com.eeos.rocatrun.viewmodel

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object BossHealthRepository {
    // 초기 최대 체력은 0으로 시작하며, /first_boss_health 이벤트에서 설정됨
    private val _maxBossHealth = MutableStateFlow<Int>(0)
    val maxBossHealth: StateFlow<Int> get() = _maxBossHealth

    // 현재 보스 체력
    private val _bossHealth = MutableStateFlow<Int>(0)
    val bossHealth: StateFlow<Int> get() = _bossHealth

    // 제한시간
    private val _gameTime = MutableStateFlow(0)
    val gameTime : StateFlow<Int> get() = _gameTime

    private var countdownJob: Job? = null  // 제한시간 감소를 관리하는 Job

    fun updateBossHealth(newHealth: Int) {
        _bossHealth.value = newHealth
        // 최대 체력이 아직 설정되지 않았다면 초기값으로 설정
        if (_maxBossHealth.value == 0) {
            _maxBossHealth.value = newHealth
        }
        Log.d("BossHealthRepository", "Boss health updated: $newHealth, max: ${_maxBossHealth.value}")
    }

    fun updateMaxBossHealth(newMax: Int) {
        _maxBossHealth.value = newMax
        Log.d("BossHealthRepository", "Max boss health updated: $newMax")
    }

    fun setGameTime(time : Int){
        _gameTime.value = time + 4
        startCountdown()
        Log.d("BossHealthRepository", "제한 시간 : $time")
    }

    private fun startCountdown() {
        countdownJob?.cancel()  // 만약 카운트 다운이 있다면 중지

        countdownJob = CoroutineScope(Dispatchers.IO).launch {
            while (_gameTime.value > 0) {
                delay(1000)  // 1초마다 감소
                _gameTime.value -= 1
            }
        }
    }

}