package com.eeos.rocatrun.home.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel() {

    private val _homeData = MutableLiveData<HomeInfoResponse>()
    val homeData: LiveData<HomeInfoResponse> = _homeData

    private val retrofitInstance = RetrofitInstance.getInstance().create(HomeAPI::class.java)

//    fun fetchHomeInfo() {
//        retrofitInstance.getHomeInfo().enqueue(object : Callback<HomeInfoResponse> {
//            override fun onResponse(call: Call<HomeInfoResponse>, response: Response<HomeInfoResponse>) {
//                if (response.isSuccessful) {
//                    _homeData.value = response.body()
//                } else {
//                    println("Error: ${response.errorBody()}")
//                }
//            }
//
//            override fun onFailure(call: Call<HomeInfoResponse>, t: Throwable) {
//                Log.d("mock api", t.localizedMessage)
//            }
//        })
//    }


    // home info api
    fun fetchHomeInfo(auth: String?) {
        if (auth != null) {
            Log.d("api", auth)
            Log.d("api", "메인페이지 호출 시작")
            retrofitInstance.getHomeInfo(auth).enqueue(object : Callback<HomeInfoResponse> {
                override fun onResponse(call: Call<HomeInfoResponse>, response: Response<HomeInfoResponse>) {
                    if (response.isSuccessful) {
                        _homeData.value = response.body()
                        Log.d("api", "성공")
                    } else {
                        println("Error: ${response.errorBody()}")
                        Log.d("api", response.toString())
                    }
                }

                override fun onFailure(call: Call<HomeInfoResponse>, t: Throwable) {
                    Log.d("mock api", t.localizedMessage)
                    Log.d("api", "???")
                }
            })
        }
        else {
            Log.d("api", "토큰이 없습니다.")
        }
    }

}