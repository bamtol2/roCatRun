package com.eeos.rocatrun.presentation

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// GameState.kt
object GameState {
    private const val MAX_ITEM_GAUGE = 100
    private const val FEVER_MULTIPLIER = 2
    private const val FEVER_DURATION = 30_000L // 30초

    val currentDistance = MutableLiveData(0.0)
    val itemGauge = MutableLiveData(0)
    val bossHP = MutableLiveData(0)
    val feverActive = MutableLiveData(false)
    var itemUsageCount = 0
        private set

    private var accumulatedDistance = 0.0

    fun updateDistance(newDistance: Double) {
        currentDistance.value = newDistance
        bossHP.value = (newDistance * 1000).toInt() // 1km = 1000HP

        val requiredDistance = if (feverActive.value == true) 0.375 else 0.75 // 피버 시 375m
        accumulatedDistance += newDistance - (currentDistance.value ?: 0.0)

        if (accumulatedDistance >= requiredDistance) {
            itemGauge.value = MAX_ITEM_GAUGE
            accumulatedDistance = 0.0
        }
    }

    fun useItem() {
        itemUsageCount++
        if (itemUsageCount >= 2) {
            activateFeverTime()
            itemUsageCount = 0
        }
    }

    private fun activateFeverTime() {
        feverActive.value = true
        CoroutineScope(Dispatchers.Main).launch {
            delay(FEVER_DURATION)
            feverActive.value = false
        }
    }
}
