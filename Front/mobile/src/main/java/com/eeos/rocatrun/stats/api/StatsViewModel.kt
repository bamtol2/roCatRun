package com.eeos.rocatrun.stats.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsViewModel : ViewModel() {
    // daily API 로딩 상태 관리
    private val _dailyLoading = MutableLiveData<Boolean>(false)
    val dailyLoading: LiveData<Boolean> = _dailyLoading

    // week API 로딩 상태 관리
    private val _weekLoading = MutableLiveData<Boolean>(false)
    val weekLoading: LiveData<Boolean> = _weekLoading

    // daily Data
    private val _dailyStatsData = MutableLiveData<DailyStatsResponse>()
    val dailyStatsData: LiveData<DailyStatsResponse> = _dailyStatsData

    // week Data
    private val _weekStatsData = MutableLiveData<WeekStatsResponse>()
    val weekStatsData: LiveData<WeekStatsResponse> = _weekStatsData

    private val retrofitInstance = RetrofitInstance.getInstance().create(StatsAPI::class.java)

    // daily api
    fun getDailyStats() {
        _dailyLoading.value = true  // API 호출 시작시 로딩 상태 true

        retrofitInstance.getDailyStats().enqueue(object : Callback<DailyStatsResponse> {
            override fun onResponse(call: Call<DailyStatsResponse>, response: Response<DailyStatsResponse>) {
                if (response.isSuccessful) {
                    _dailyStatsData.value = response.body()
                } else {
                    println("Error: ${response.errorBody()}")
                }
                _dailyLoading.value = false
            }

            override fun onFailure(call: Call<DailyStatsResponse>, t: Throwable) {
                Log.d("mock api", t.localizedMessage)
                _dailyLoading.value = false
            }
        })
    }

    // week api
    fun getWeekStats(year: Int, month: Int, week: Int) {
        _weekLoading.value = true

        val startTime = System.currentTimeMillis()
        Log.d("StatsViewModel", "API call started at: $startTime")

        retrofitInstance.getWeekStats(year, month, week).enqueue(object : Callback<WeekStatsResponse> {
            override fun onResponse(call: Call<WeekStatsResponse>, response: Response<WeekStatsResponse>) {
                if (response.isSuccessful) {
                    _weekStatsData.value = response.body()

                    val endTime = System.currentTimeMillis()
                    Log.d("StatsViewModel", "API call finished at: $endTime")
                    Log.d("StatsViewModel", "Time taken for API call: ${endTime - startTime} ms")
                } else {
                    println("Error: ${response.errorBody()}")
                }
                _weekLoading.value = false
            }

            override fun onFailure(call: Call<WeekStatsResponse>, t: Throwable) {
                Log.d("mock api", t.localizedMessage)
                _weekLoading.value = false
            }
        })
    }

}