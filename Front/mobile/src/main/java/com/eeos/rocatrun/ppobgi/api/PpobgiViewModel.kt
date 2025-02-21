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

    private val _showCoinShortageDialog = MutableStateFlow(false)
    val showCoinShortageDialog: StateFlow<Boolean> = _showCoinShortageDialog

    private val _isDrawing = MutableStateFlow(false)
    val isDrawing: StateFlow<Boolean> = _isDrawing

    private val _showResult = MutableStateFlow(false)
    val showResult: StateFlow<Boolean> = _showResult

    fun drawItem(token: String, drawCount: Int) {
        viewModelScope.launch {
            try {
                drawItems(token, drawCount).onSuccess { response ->
                    if (response.success) {
                        _drawResult.value = response.data.drawnItems.firstOrNull()
                        _remainingCoins.value = response.data.remainingCoins
                        _isDrawing.value = true
                        Log.d("뽑기", "drawItem: ${_drawResult.value} remainingCoins: ${_remainingCoins.value}")
                    } else {
                        _showCoinShortageDialog.value = true
                        Log.d("뽑기", "코인 부족: ${response.message}")
                    }
                }.onFailure {exception ->
                    _error.value = exception.message
                    Log.e("뽑기", "API 호출 실패: ${exception.message}")
                }
            } catch (e: Exception) {
                _error.value = "뽑기 실패: ${e.message}"
                Log.e("뽑기", "예외 발생: ${e.message}")
            }
        }
    }

    fun setShowResult(show: Boolean) {
        _showResult.value = show
        if (show) {
            _isDrawing.value = false
        }
    }

    fun dismissCoinShortageDialog() {
        _showCoinShortageDialog.value = false
    }

    private val ppobgiAPI = RetrofitInstance.getInstance().create(PpobgiAPI::class.java)

    suspend fun drawItems(token: String, drawCount: Int): Result<PpobgiResponse> {
        return try {
            Log.d("뽑기", "API 요청 시작 - drawCount: $drawCount")
            val drawRequest = DrawRequest(drawCount)
            val response = ppobgiAPI.randomPpobgi("Bearer $token", drawRequest)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("뽑기", "API 응답 성공: $responseBody")
                Result.success(responseBody!!)
            } else {
                Log.e("뽑기", "API 응답 실패: ${response.code()} - ${response.message()}")
                Result.failure(Exception("뽑기 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("뽑기", "API 호출 예외 발생: ${e.message}")
            Result.failure(e)
        }
    }

    fun clearDrawResult() {
        _drawResult.value = null
        _error.value = null
        _remainingCoins.value = null
        _showCoinShortageDialog.value = false
        _isDrawing.value = false
        _showResult.value = false
    }
}