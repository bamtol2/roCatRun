package com.eeos.rocatrun.ranking.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eeos.rocatrun.home.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingViewModel : ViewModel() {
    private val _rankingData = MutableLiveData<RankingResponse>()
    val rankingData: LiveData<RankingResponse> = _rankingData

    private val retrofitInstance = RetrofitInstance.getInstance().create(RankingAPI::class.java)

    // ranking info api
    fun fetchRankingInfo(auth: String?) {
        if (auth != null) {
            Log.d("api", "랭킹페이지 호출 시작")
            retrofitInstance.getRankingInfo("Bearer $auth").enqueue(object : Callback<RankingResponse> {
                override fun onResponse(call: Call<RankingResponse>, response: Response<RankingResponse>) {
                    if (response.isSuccessful) {
                        _rankingData.value = response.body()
                        Log.d("api", "랭킹 성공")
                    } else {
                        Log.d("api", response.toString())
                    }
                }

                override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                    Log.d("api", "Error: ${t.localizedMessage}")
                }
            })
        } else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }

}