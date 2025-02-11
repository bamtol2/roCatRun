package com.eeos.rocatrun.viewmodel

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object BossHealthRepository {
    // 초기 최대 체력은 0으로 시작하며, /first_boss_health 이벤트에서 설정됨
    private val _maxBossHealth = MutableStateFlow<Int>(0)
    val maxBossHealth: StateFlow<Int> get() = _maxBossHealth

    // 현재 보스 체력
    private val _bossHealth = MutableStateFlow<Int>(0)
    val bossHealth: StateFlow<Int> get() = _bossHealth

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
}