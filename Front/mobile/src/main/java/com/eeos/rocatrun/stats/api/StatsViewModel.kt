package com.eeos.rocatrun.stats.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.eeos.rocatrun.api.RetrofitInstance

class StatsViewModel : ViewModel() {
    // 로딩 상태 관리
    private val _dailyLoading = MutableLiveData<Boolean>(false)
    val dailyLoading: LiveData<Boolean> = _dailyLoading

    private val _weekLoading = MutableLiveData<Boolean>(false)
    val weekLoading: LiveData<Boolean> = _weekLoading

    private val _monLoading = MutableLiveData<Boolean>(false)
    val monLoading: LiveData<Boolean> = _monLoading

    // Data
    private val _noDayData = MutableLiveData<Boolean>(false)
    val noDayData: LiveData<Boolean> = _noDayData

    private val _noWeekData = MutableLiveData<Boolean>(false)
    val noWeekData: LiveData<Boolean> = _noWeekData

    private val _noMonData = MutableLiveData<Boolean>(false)
    val noMonData: LiveData<Boolean> = _noMonData

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
    fun fetchDailyStats(auth: String?) {
        _dailyLoading.value = true

        if (auth != null) {
            Log.d("api", "일별 통계 페이지 호출 시작")
            _noDayData.value = false
            retrofitInstance.getDailyStats("Bearer $auth")
                .enqueue(object : Callback<DailyStatsResponse> {
                    override fun onResponse(
                        call: Call<DailyStatsResponse>,
                        response: Response<DailyStatsResponse>
                    ) {
                        if (response.isSuccessful) {
                            _dailyStatsData.value = response.body()
                            Log.d("api", "일별 통계 호출 성공")
                        } else {
                            if (response.code() == 404 || response.code() == 400) {
                                _noDayData.value = true
                            } else {
                                Log.d("api", response.toString())
                            }
                        }
                        _dailyLoading.value = false
                    }

                    override fun onFailure(call: Call<DailyStatsResponse>, t: Throwable) {
                        Log.d("mock api", t.localizedMessage)
                        _dailyLoading.value = false
                    }
                })
        } else {
            Log.d("api", "토큰이 없습니다.")
            _dailyLoading.value = false
        }
    }

    // week api
    fun fetchWeekStats(auth: String?, date: String, week: Int) {
        _weekLoading.value = true

        if (auth != null) {
            Log.d("api", "주별 통계 페이지 호출 시작")
            _noWeekData.value = false
            retrofitInstance.getWeekStats("Bearer $auth", date, week)
                .enqueue(object : Callback<WeekMonStatsResponse> {
                    override fun onResponse(
                        call: Call<WeekMonStatsResponse>,
                        response: Response<WeekMonStatsResponse>
                    ) {
                        if (response.isSuccessful) {
                            _weekStatsData.value = response.body()
                            Log.d("api", "주별 통계 호출 성공")
                        } else {
                            if (response.code() == 404 || response.code() == 400) {
                                _noWeekData.value = true
                            } else {
                                Log.d("api", response.toString())
                            }
                        }
                        _weekLoading.value = false
                    }

                    override fun onFailure(call: Call<WeekMonStatsResponse>, t: Throwable) {
                        Log.d("mock api", t.localizedMessage)
                        _weekLoading.value = false
                    }
                })
        } else {
            Log.d("api", "토큰이 없습니다.")
            _weekLoading.value = false
        }
    }

    // mon api
    fun fetchMonStats(auth: String?, date: String) {
        _monLoading.value = true

        if (auth != null) {
            Log.d("api", "월별 통계 페이지 호출 시작")
            _noMonData.value = false
            retrofitInstance.getMonStats("Bearer $auth", date)
                .enqueue(object : Callback<WeekMonStatsResponse> {
                    override fun onResponse(
                        call: Call<WeekMonStatsResponse>,
                        response: Response<WeekMonStatsResponse>
                    ) {
                        if (response.isSuccessful) {
                            _monStatsData.value = response.body()
                            Log.d("api", "월별 통계 호출 성공")
                        } else {
                            if (response.code() == 404 || response.code() == 400) {
                                _noMonData.value = true
                            } else {
                                Log.d("api", response.toString())
                            }
                        }
                        _monLoading.value = false
                    }

                    override fun onFailure(call: Call<WeekMonStatsResponse>, t: Throwable) {
                        Log.d("mock api", t.localizedMessage)
                        _monLoading.value = false
                    }
                })
        } else {
            Log.d("api", "토큰이 없습니다.")
            _weekLoading.value = false
        }
    }

}