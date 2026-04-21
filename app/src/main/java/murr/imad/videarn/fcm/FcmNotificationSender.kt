package murr.imad.videarn.fcm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import murr.imad.videarn.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Sends a push notification to a specific device via FCM.
 *
 * This is a pure suspend function — it has no dependency on Activity or lifecycle.
 * Call it from a ViewModel's viewModelScope or a Repository's coroutine context.
 *
 * @param recipientToken The FCM device token of the recipient.
 * @param title The notification title.
 * @param message The notification message body.
 * @return [Result.success] on a 2xx response, [Result.failure] on network error or non-2xx.
 */
suspend fun sendFcmNotification(
    recipientToken: String,
    title: String,
    message: String
): Result<Unit> = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

        val payload = JSONObject().apply {
            put(Constants.FCM_KEY_TO, recipientToken)
            put(Constants.FCM_KEY_DATA, JSONObject().apply {
                put(Constants.FCM_KEY_TITLE, title)
                put(Constants.FCM_KEY_MESSAGE, message)
            })
        }

        val requestBody = payload.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(Constants.FCM_BASE_URL)
            .addHeader("Authorization", "Bearer ${Constants.FCM_SERVER_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(IOException("FCM request failed: ${response.code} ${response.message}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}