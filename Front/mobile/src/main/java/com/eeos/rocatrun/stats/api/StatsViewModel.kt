package com.eeos.rocatrun.stats.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsViewModel : ViewModel() {
    // 로딩 상태 관리
    private val _dailyLoading = MutableLiveData<Boolean>(false)
    val dailyLoading: LiveData<Boolean> = _dailyLoading

    private val _weekLoading = MutableLiveData<Boolean>(false)
    val weekLoading: LiveData<Boolean> = _weekLoading

    private val _monLoading = MutableLiveData<Boolean>(false)
    val monLoading: LiveData<Boolean> = _monLoading

    // Data
    private val _dailyStatsData = MutableLiveData<DailyStatsResponse>()
    val dailyStatsData: LiveData<DailyStatsResponse> = _dailyStatsData

    private val _weekStatsData = MutableLiveData<WeekMonStatsResponse>()
    val weekStatsData: LiveData<WeekMonStatsResponse> = _weekStatsData

    private val _monStatsData = MutableLiveData<WeekMonStatsResponse>()
    val monStatsData: LiveData<WeekMonStatsResponse> = _monStatsData


    // Week 날짜 (초기 값, 업데이트)
    var selectedWeekDate by mutableStateOf(getCurrentWeek())
        private set

    private fun getCurrentWeek(): String {
        val currentDate = android.icu.util.Calendar.getInstance()
        val year = currentDate.get(android.icu.util.Calendar.YEAR)
        val month = currentDate.get(android.icu.util.Calendar.MONTH) + 1
        val week = currentDate.get(android.icu.util.Calendar.WEEK_OF_MONTH)
        return "${year}년 ${month}월 ${week}주"
    }

    fun updateWeekDate(newDate: String) {
        selectedWeekDate = newDate
    }

    // Mon 날짜
    var selectedMonDate by mutableStateOf(getCurrentMon())
        private set

    private fun getCurrentMon(): String {
        val currentDate = android.icu.util.Calendar.getInstance()
        val year = currentDate.get(android.icu.util.Calendar.YEAR)
        val month = currentDate.get(android.icu.util.Calendar.MONTH) + 1
        return "${year}년 ${month}월"
    }

    fun updateMonDate(newDate: String) {
        selectedMonDate = newDate
    }


    // retrofit 인스턴스
    private val retrofitInstance = RetrofitInstance.getInstance().create(StatsAPI::class.java)

    // daily api
    fun fetchDailyStats() {
        _dailyLoading.value = true

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
    fun fetchWeekStats(year: Int, month: Int, week: Int) {
        _weekLoading.value = true

        val startTime = System.currentTimeMillis()
        Log.d("StatsViewModel", "Weeeeeeeek API call started at: $startTime")

        retrofitInstance.getWeekStats(year, month, week).enqueue(object : Callback<WeekMonStatsResponse> {
            override fun onResponse(call: Call<WeekMonStatsResponse>, response: Response<WeekMonStatsResponse>) {
                if (response.isSuccessful) {
                    _weekStatsData.value = response.body()

                    val endTime = System.currentTimeMillis()
                    Log.d("StatsViewModel", "Weeeeeeeek API call finished at: $endTime")
                    Log.d("StatsViewModel", "Weeeeeeeek Time taken for API call: ${endTime - startTime} ms")
                } else {
                    println("Error: ${response.errorBody()}")
                }
                _weekLoading.value = false
            }

            override fun onFailure(call: Call<WeekMonStatsResponse>, t: Throwable) {
                Log.d("mock api", t.localizedMessage)
                _weekLoading.value = false
            }
        })
    }

    // mon api
    fun fetchMonStats(year: Int, month: Int) {
        _monLoading.value = true

        val startTime = System.currentTimeMillis()
        Log.d("StatsViewModel", "API call started at: $startTime")

        retrofitInstance.getMonStats(year, month).enqueue(object : Callback<WeekMonStatsResponse> {
            override fun onResponse(call: Call<WeekMonStatsResponse>, response: Response<WeekMonStatsResponse>) {
                if (response.isSuccessful) {
                    _monStatsData.value = response.body()

                    val endTime = System.currentTimeMillis()
                    Log.d("StatsViewModel", "API call finished at: $endTime")
                    Log.d("StatsViewModel", "Time taken for API call: ${endTime - startTime} ms")
                } else {
                    println("Error: ${response.errorBody()}")
                }
                _monLoading.value = false
            }

            override fun onFailure(call: Call<WeekMonStatsResponse>, t: Throwable) {
                Log.d("mock api", t.localizedMessage)
                _monLoading.value = false
            }
        })
    }

}