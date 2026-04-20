package murr.imad.myapplication.fcm

import android.app.Activity
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import murr.imad.myapplication.R
import murr.imad.myapplication.payouts.ui.view.activities.CryptoPayoutActivity
import murr.imad.myapplication.shared.ui.view.MainActivity
import murr.imad.myapplication.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class PostRequestForNotifications(
    private val referredUserName : String,
    private val referralUserToken: String,
    private val activity: Activity,
    private val title: String,
    private val message: String
) {

    fun startApiCall() {
        when(activity){
            is MainActivity -> {
                activity.runOnUiThread {
                    activity.showProgressDialog(activity.resources.getString(R.string.please_wait))
                }
                activity.lifecycleScope.launch(Dispatchers.IO) {
                    val stringResult = makeApiCall()
                    afterCallFinish(stringResult)
                }
            }
            is CryptoPayoutActivity -> {
                activity.runOnUiThread {
                    activity.showProgressDialog(activity.resources.getString(R.string.please_wait))
                }
                activity.lifecycleScope.launch(Dispatchers.IO) {
                    val stringResult = makeApiCall()
                    afterCallFinish(stringResult)
                }
            }

        }

    }

    private fun makeApiCall(): String {
        val client = OkHttpClient()

        // Create JSON payload
        val jsonRequest = JSONObject().apply {
            put(Constants.FCM_KEY_TO, referralUserToken)
            put(Constants.FCM_KEY_DATA, JSONObject().apply {
                put(Constants.FCM_KEY_TITLE, title)
                put(Constants.FCM_KEY_MESSAGE, "$referredUserName $message")
            })
        }

        // Build the request body
        val requestBody = jsonRequest.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Build the HTTP request
        val request = Request.Builder()
            .url(Constants.FCM_BASE_URL)
            .addHeader("Authorization", "Bearer ${Constants.FCM_SERVER_KEY}") // Proper Bearer token usage
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // Successful response
                response.body?.string() ?: "Success but empty response"
            } else {
                // Failed response
                "Error: ${response.message}"
            }
        } catch (e: IOException) {
            // Network error handling
            "Error: ${e.message}"
        }
    }

    private fun afterCallFinish(result: String?) {
        when(activity){
            is MainActivity ->{
                activity.runOnUiThread { activity.hideProgressDialog() }
            }
            is CryptoPayoutActivity -> {
                activity.runOnUiThread { activity.hideProgressDialog() }
            }
        }

        Log.i("JSON RESPONSE RESULT", result.toString())
    }

}