package com.eeos.rocatrun.profile.api

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileViewModel : ViewModel() {

    private val _profileData = MutableLiveData<ProfileResponse>()
    val profileData: LiveData<ProfileResponse> = _profileData

    private val retrofitInstance = RetrofitInstance.getInstance().create(ProfileAPI::class.java)

    // 회원 정보 조회
    fun fetchProfileInfo(auth: String?) {
        if (auth != null) {
            Log.d("api", auth)
            Log.d("api", "마이페이지 호출 시작")
            retrofitInstance.getProfileInfo(auth).enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        _profileData.value = response.body()
                        Log.d("api", _profileData.value?.data?.nickname ?: "")
                    } else {
                        println("Error: ${response.errorBody()}")
                        Log.d("api", response.toString())
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.d("api", t.localizedMessage)
                }
            })
        }
        else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }

    // 로그아웃
    fun fetchLogout(auth: String?) {
        if (auth != null) {
            Log.d("api", auth)
            Log.d("api", "로그아웃 호출 시작")
            retrofitInstance.userLogout(auth).enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.d("api", "로그아웃 성공")
                    } else {
                        println("Error: ${response.errorBody()}")
                        Log.d("api", response.toString())
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.d("api", t.localizedMessage)
                }
            })
        }
        else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }
}