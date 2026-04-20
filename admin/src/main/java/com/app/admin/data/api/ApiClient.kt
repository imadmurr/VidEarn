package com.app.admin.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://127.0.0.1:4000/us-central1-earning-app-f3ab1.cloudfunctions.net/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val payoutApiService: PayoutsApiService by lazy {
        retrofit.create(PayoutsApiService::class.java)
    }
}
