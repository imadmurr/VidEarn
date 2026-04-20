package murr.imad.myapplication.fcm

import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.utils.Constants

class MyCloudMessagingToken(private val userRepository: UserRepository) {

    /**
     * Checks if the FCM token is updated in Firestore, and updates if necessary.
     */
    fun checkForFCMTokenUpdates(
        sharedPreferences: SharedPreferences
    ) {
        val tokenUpdated = sharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if (tokenUpdated) {
            // Token is already updated in Firestore.
        } else {
            // Get a new FCM token and update Firestore.
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    updateFCMToken(token, sharedPreferences)
                }
        }
    }

    /**
     * Updates the FCM token in Firestore using UserRepository.
     */
    private fun updateFCMToken(
        token: String,
        sharedPreferences: SharedPreferences
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val userHashMap = hashMapOf<String, Any>(Constants.FCM_TOKEN to token)

            // Use UserRepository's suspend function to update the FCM token in Firestore.
            val result = userRepository.updateUserData(userHashMap)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    // Mark token as updated in shared preferences.
                    sharedPreferences.edit()
                        .putBoolean(Constants.FCM_TOKEN_UPDATED, true)
                        .apply()
                }
            }
        }
    }

    /**
     * Clears the FCM token and other data from SharedPreferences.
     */
    fun clearFCMToken(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().clear().apply()
    }
}
