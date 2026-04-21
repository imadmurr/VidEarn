package murr.imad.videarn.internetsharing.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import murr.imad.videarn.auth.data.repository.UserRepository
import murr.imad.videarn.internetsharing.ui.view.InternetSharingService

/**
 * ViewModel to handle the logic of InternetSharingFragment.
 */
class InternetSharingViewModel() : ViewModel() {

    private val _permissionsAllowed = MutableLiveData<Boolean>()
    val permissionsAllowed: LiveData<Boolean> get() = _permissionsAllowed

    private val userRepository = UserRepository()



    private fun startInternetSharingService(context: Context) {
        val serviceIntent = Intent(context, InternetSharingService::class.java).apply {
            putExtra("userId", userRepository.getCurrentUserID())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun stopInternetSharingService(context: Context) {
        context.stopService(Intent(context, InternetSharingService::class.java).apply {
            putExtra("userId", userRepository.getCurrentUserID())
        })
    }

    /**
     * Checks if battery optimization is enabled.
     */
    fun isBatteryOptimizationEnabled(context: Context): Boolean {
        val powerManager = context.getSystemService(PowerManager::class.java)
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Opens the main settings page for the app.
     */
    fun openMainSettings(context: Context) {
        val packageName = context.packageName
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:$packageName")
        }
        context.startActivity(intent)
    }
}
