package com.app.admin.data.repository

import android.util.Log
import com.app.admin.data.api.ApiClient
import com.app.admin.data.api.PayoutResponse
import com.app.admin.data.model.Payout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayoutsRepository {

    /**
     * Fetches all gift card payouts using Retrofit.
     */
    fun getGiftCardPayouts(onSuccess: (List<Payout.GiftCard>?) -> Unit, onError: (String?) -> Unit) {
        ApiClient.payoutApiService.getGiftCardPayouts().enqueue(object : Callback<PayoutResponse> {
            override fun onResponse(call: Call<PayoutResponse>, response: Response<PayoutResponse>) {
                if (response.isSuccessful) {
                    val payoutResponse = response.body()
                    if (payoutResponse?.success == true) {
                        val payouts: List<Payout.GiftCard> = payoutResponse.data as List<Payout.GiftCard>
                        // Pass the payouts list to the success callback
                        onSuccess(payouts)
                    } else {
                        // Handle error from the server
                        onError(payoutResponse?.error)
                    }
                } else {
                    // Handle response failure
                    onError("Response failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PayoutResponse>, t: Throwable) {
                // Handle network or other errors
                Log.e("PayoutsRepository", "Error fetching gift card payouts", t)
                onError(t.message)
            }
        })
    }

    /**
     * Fetches all crypto payouts using Retrofit.
     */
    fun getCryptoPayouts(onSuccess: (List<Payout>?) -> Unit, onError: (String?) -> Unit) {
        ApiClient.payoutApiService.getCryptoPayouts().enqueue(object : Callback<PayoutResponse> {
            override fun onResponse(call: Call<PayoutResponse>, response: Response<PayoutResponse>) {
                if (response.isSuccessful) {
                    val payoutResponse = response.body()
                    if (payoutResponse?.success == true) {
                        val payouts = payoutResponse.data
                        // Pass the crypto payouts to the success callback
                        onSuccess(payouts)
                    } else {
                        // Handle error from the server
                        onError(payoutResponse?.error)
                    }
                } else {
                    // Handle response failure
                    onError("Response failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PayoutResponse>, t: Throwable) {
                // Handle network or other errors
                Log.e("PayoutsRepository", "Error fetching crypto payouts", t)
                onError(t.message)
            }
        })
    }
}
