package com.eeos.rocatrun.home.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.eeos.rocatrun.api.RetrofitInstance


class HomeViewModel : ViewModel() {

    private val _homeData = MutableLiveData<HomeInfoResponse>()
    val homeData: LiveData<HomeInfoResponse> = _homeData

    private val retrofitInstance = RetrofitInstance.getInstance().create(HomeAPI::class.java)

    // home info api
    fun fetchHomeInfo(auth: String?) {
        if (auth != null) {
            Log.d("api", "메인페이지 호출 시작")
            retrofitInstance.getHomeInfo("Bearer $auth").enqueue(object : Callback<HomeInfoResponse> {
                override fun onResponse(call: Call<HomeInfoResponse>, response: Response<HomeInfoResponse>) {
                    if (response.isSuccessful) {
                        _homeData.value = response.body()
                        Log.d("api", "메인 성공 ${_homeData.value.toString()}")
                    } else {
                        Log.d("api", "메인 실패 ${response.toString()}")
                    }
                }

                override fun onFailure(call: Call<HomeInfoResponse>, t: Throwable) {
                    Log.d("api", t.localizedMessage)
                }
            })
        }
        else {
            Log.d("api", "토큰이 없습니다.")
        }
    }

}