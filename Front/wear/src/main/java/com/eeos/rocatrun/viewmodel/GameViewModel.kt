package com.eeos.rocatrun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class GameViewModel : ViewModel() {

    private val _itemGauge = MutableLiveData(0)
    val itemGauge: LiveData<Int> = _itemGauge

    fun updateItemGauge(value: Int) {
        _itemGauge.value = value
    }
}
