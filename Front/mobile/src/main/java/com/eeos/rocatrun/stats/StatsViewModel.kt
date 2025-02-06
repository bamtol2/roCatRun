package com.eeos.rocatrun.stats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsViewModel : ViewModel() {
    private val _statsData = MutableLiveData<StatsResponse>()
    val statsData: LiveData<StatsResponse> = _statsData

//    init {
//        fetchDailyStats()
//    }

    // 로딩 상태 관리
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    fun fetchDailyStats() {
        _loading.value = true  // API 호출 시작시 로딩 상태 true

        val startTime = System.currentTimeMillis()
        Log.d("StatsViewModel", "API call started at: $startTime")

        val retrofitInstance = RetrofitInstance.getInstance().create(StatsAPI::class.java)
        retrofitInstance.getDailyStats().enqueue(object : Callback<StatsResponse> {
            override fun onResponse(call: Call<StatsResponse>, response: Response<StatsResponse>) {
                if (response.isSuccessful) {
                    Log.d("mock api", "getData onResponse()")
                    _statsData.value = response.body()

                    val endTime = System.currentTimeMillis()
                    Log.d("StatsViewModel", "API call finished at: $endTime")
                    Log.d("StatsViewModel", "Time taken for API call: ${endTime - startTime} ms")
                } else {
                    println("Error: ${response.errorBody()}")
                }
                _loading.value = false  // 로딩 상태 false로 변경
            }

            override fun onFailure(call: Call<StatsResponse>, t: Throwable) {
                Log.d("mock api", t.localizedMessage)
            }
        })
    }
}