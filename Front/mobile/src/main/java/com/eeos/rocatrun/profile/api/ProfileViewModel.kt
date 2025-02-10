package com.eeos.rocatrun.profile.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileViewModel : ViewModel() {

    private val _profileData = MutableLiveData<ProfileResponse>()
    val profileData: LiveData<ProfileResponse> = _profileData

    private val retrofitInstance = RetrofitInstance.getInstance().create(ProfileAPI::class.java)

    fun fetchProfileInfo(auth: String?) {
        if (auth != null) {
            Log.d("api", auth)
            Log.d("api", "호출 시작")
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
                    Log.d("mock api", t.localizedMessage)
                }
            })
        }
        else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }
}