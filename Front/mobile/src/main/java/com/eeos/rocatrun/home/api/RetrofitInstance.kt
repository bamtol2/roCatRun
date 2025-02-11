package com.eeos.rocatrun.home.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://i12e205.p.ssafy.io:8081/"

//    private const val BASE_URL ="https://df6e090f-b219-448a-ae4a-a8323b5b366e.mock.pstmn.io/" // 가상데이터 주소

    private val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInstance(): Retrofit {
        return client
    }
}
