package com.app.admin.data.api

import retrofit2.Call
import retrofit2.http.GET

interface PayoutsApiService {
    @GET("getGiftCardPayouts")  // Cloud Function URL for GiftCard payouts
    fun getGiftCardPayouts(): Call<PayoutResponse>

    @GET("getCryptoPayouts")    // Cloud Function URL for Crypto payouts
    fun getCryptoPayouts(): Call<PayoutResponse>
}

