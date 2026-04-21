package murr.imad.videarn.internetsharing.ui.view.utils.network

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class ServiceUtils {

    /**
     * Saves the daily earnings to SharedPreferences.
     * @param context The application context.
     * @param userId The unique identifier of the user.
     * @param earnedAmount The amount earned by the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveDailyEarnings(context: Context, userId: String, earnedAmount: Float) {
        val preferences = context.getSharedPreferences("user_earnings", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        val today = LocalDate.now().toString()

        // Use the user ID and today's date as a key
        editor.putFloat("$userId-$today", earnedAmount)
        editor.apply()
    }

    /**
     * Retrieves the daily earnings from SharedPreferences.
     * @param context The application context.
     * @param userId The unique identifier of the user.
     * @return The amount earned today by the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyEarnings(context: Context, userId: String): Float {
        val preferences = context.getSharedPreferences("user_earnings", Context.MODE_PRIVATE)

        val today = LocalDate.now().toString()

        // Use the user ID and today's date as a key
        return preferences.getFloat("$userId-$today", 0.0f)
    }

    /**
     * Displays the daily earnings in the service notification.
     * @param context The application context.
     * @param earnedAmountToday The amount earned by the user today.
     */
    fun showNotification(context: Context, earnedAmountToday: Float) {
        // Implementation for displaying in the notification...
    }

    /**
     * Placeholder function for reward calculation logic.
     * Replace this with your actual calculation logic.
     * @return The calculated reward amount.
     */
    fun calculateReward(): Float {
        // Replace with your reward calculation logic
        return 5.0f
    }

    /**
     * Checks if a specified service is currently running.
     *
     * This function queries the list of running services to determine if the specified service is active.
     * Note: Starting from Android 8.0 (API level 26), the use of `getRunningServices` is restricted for
     * privacy and security reasons. Consider using more modern approaches like `JobIntentService` or
     * `JobScheduler` for background tasks when targeting Android 8.0 and above.
     *
     * @param context The application context.
     * @param serviceClass The class of the service to check.
     * @return `true` if the specified service is running, `false` otherwise.
     */
    fun isServiceRunning(context: Context,
                         serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?

        // Get the list of running services
        val runningServices =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager?.getRunningServices(Int.MAX_VALUE)
            } else {
                @Suppress("DEPRECATION")
                manager?.getRunningServices(Int.MAX_VALUE)
            }

        // Check if the service is in the list
        if (runningServices != null) {
            for (service in runningServices) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        }

        return false
    }

}