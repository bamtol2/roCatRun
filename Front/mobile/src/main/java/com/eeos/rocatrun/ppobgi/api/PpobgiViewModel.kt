package com.eeos.rocatrun.ppobgi.api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eeos.rocatrun.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PpobgiViewModel : ViewModel() {

    private val _drawResult = MutableStateFlow<DrawItem?>(null)
    val drawResult: StateFlow<DrawItem?> = _drawResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _remainingCoins = MutableStateFlow<Int?>(null)
    val remainingCoins: StateFlow<Int?> = _remainingCoins

    fun drawItem(token: String, drawCount: Int) {
        viewModelScope.launch {
            try {
                drawItems(token, drawCount).onSuccess { response ->
                    if (response.success) {
                        _drawResult.value = response.data.drawnItems.firstOrNull()
                        _remainingCoins.value = response.data.remainingCoins

                        Log.d("뽑기", "drawItem: ${_drawResult.value} ${_remainingCoins.value}")
                    } else {
                        _error.value = response.message
                    }
                }.onFailure {
                    _error.value = it.message
                }
            } catch (e: Exception) {
                _error.value = "뽑기 실패: ${e.message}"
            }
        }
    }

    private val ppobgiAPI = RetrofitInstance.getInstance().create(PpobgiAPI::class.java)

    suspend fun drawItems(token: String, drawCount: Int): Result<PpobgiResponse> {
        return try {
            val response = ppobgiAPI.randomPpobgi("Bearer $token", drawCount)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("뽑기 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}