package com.eeos.rocatrun.closet.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ClosetViewModel : ViewModel() {
    // 전송 완료 응답 데이터
    private val _sendImageResult = MutableLiveData<UploadResponse>()
    val sendImageResult: LiveData<UploadResponse> = _sendImageResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val retrofitInstance = RetrofitInstance.getInstance().create(ClosetAPI::class.java)

    // 이미지 전송
    fun sendCharacterImage(auth: String?, filePath: String) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 파일 생성
                val file = File(filePath)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    requestFile
                )

                // API 호출
                val response = retrofitInstance.uploadImage(
                    token = "Bearer $auth",
                    image = imagePart
                )

                if (response.isSuccessful) {
                    _sendImageResult.value = response.body()
                    Log.d("api", "업로드 성공: ${_sendImageResult.value}")
                } else {
                    Log.e("api", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("api", "Exception: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}