package murr.imad.myapplication.internetsharing.ui.view.utils.network

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dev.jahidhasanco.networkusage.Interval
import dev.jahidhasanco.networkusage.NetworkType
import dev.jahidhasanco.networkusage.NetworkUsageManager
import dev.jahidhasanco.networkusage.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import murr.imad.myapplication.auth.data.repository.UserRepository
import java.util.concurrent.TimeUnit

/**
 * Class responsible for periodically checking shared traffic, comparing it with the previous
 * checked traffic, and rewarding the user if needed.
 *
 * @param context The application context.
 */
class TrafficChecker(private val context: Context) {

    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("TrafficPrefs", Context.MODE_PRIVATE)
    }

    private val KEY_LAST_RESET_DATE = "key_last_reset_date"

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val userId = UserRepository().getCurrentUserID()
    private val networkUsage = MyNetworkUsageManager(
        NetworkUsageManager(context, Util.getSubscriberId(context)),context)



    /**
     * Start the traffic checking process by scheduling it every 30 minutes.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun startTrafficChecking() {
        coroutineScope.launch {
            while (isActive) {
                delay(TimeUnit.MINUTES.toMillis(30))
                checkAndRewardUser()
            }
        }
    }

    /**
     * Check shared traffic, compare it with the previous checked traffic, and reward the user if needed.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun checkAndRewardUser() {
        val currentTraffic = getSharedTraffic()
        val previousTraffic = sharedPreferences.getLong("previousTraffic", 0L)

        if (currentTraffic > previousTraffic) {
            val trafficDifference = currentTraffic - previousTraffic
            val reward = getPointsToReward(trafficDifference)
            println("Traffic Points: $reward")
            rewardUser(reward)
            sharedPreferences.edit().putLong("previousTraffic", currentTraffic).apply()
        }
    }

    /**
     * Get the current shared traffic.
     *
     * @return The current shared traffic.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getSharedTraffic(): Long {
        return getCurrentSharedTraffic()
    }

    /**
     * Reward the user based on the provided traffic difference.
     *
     * @param trafficDifference The difference in shared traffic.
     */
    private suspend fun rewardUser(trafficDifference: Long) {
        withContext(Dispatchers.IO) {
            try {
                rewardUserInFirestore(trafficDifference)
                Log.d("TrafficChecker", "User rewarded with $trafficDifference points")
            } catch (e: Exception) {
                Log.e("TrafficChecker", "Rewarding user failed: ${e.message}")
            }
        }
    }

    /**
     * Get the current shared traffic. (Replace with the actual implementation)
     *
     * @return The current shared traffic.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getCurrentSharedTraffic(): Long {
        val todayTraffic = networkUsage.getForegroundUsageForMyApp(Interval.today,
            NetworkType.ALL)
        return todayTraffic.uploads/2L // Placeholder, replace with the actual implementation
    }

    /**
     * Reward the user in Firestore based on the provided traffic difference.
     * (Replace with the actual implementation)
     *
     * @param trafficDifference The difference in shared traffic.
     */
    private suspend fun rewardUserInFirestore(trafficDifference: Long) {
        // Placeholder, replace with the actual implementation
        // Assuming you have a "users" collection with a "points" field
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users")
                .document(userId)
                .update("points", FieldValue.increment(trafficDifference))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stop the traffic checking process.
     */
    fun stopTrafficChecking() {
        coroutineScope.cancel()
    }

    private fun getLastResetDate(): Long {
        return sharedPreferences.getLong(KEY_LAST_RESET_DATE, 0)
    }

    fun setLastResetDate(date: Long) {
        sharedPreferences.edit().putLong(KEY_LAST_RESET_DATE, date).apply()
    }

    fun isNewDay(): Boolean {
        val lastResetDate = getLastResetDate()

        return if (lastResetDate == 0L) {
            // First time, consider it a new day
            true
        } else {
            (System.currentTimeMillis() - lastResetDate) >= 86400000 /*86400000*/
        }
    }

}
