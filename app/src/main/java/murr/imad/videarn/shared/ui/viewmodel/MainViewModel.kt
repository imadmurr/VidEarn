package murr.imad.videarn.shared.ui.viewmodel

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.auth.data.repository.UserRepository
import murr.imad.videarn.auth.ui.view.SignInActivity
import murr.imad.videarn.fcm.MyCloudMessagingToken
import murr.imad.videarn.shared.ui.view.MainActivity
import murr.imad.videarn.splashonboarding.ui.SplashActivity

/**
 * ViewModel for managing the data and business logic in the MainActivity.
 */
class MainViewModel() : ViewModel() {

    private val userRepository = UserRepository()
    private val myCloudMessagingToken = MyCloudMessagingToken(userRepository)

    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> get() = _userDetails


    init {
        fetchUserDetails()
    }

    /**
     * Fetches user details from the repository using Flow and updates LiveData.
     */
    private fun fetchUserDetails() {
        viewModelScope.launch {
            userRepository.readUserDataFlow().collect { result ->
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _userDetails.postValue(user)
                } else {
                    _userDetails.postValue(null)
                }
            }
        }
    }

    /**
     * Checks for and requests an update if available.
     *
     */
    fun checkForAppUpdate(activity: MainActivity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    activity,
                    SplashActivity.APP_UPDATE_REQUEST_CODE
                )
            }
        }
    }

    /**
     * Checks for and updates the FCM token if necessary.
     *
     * @param sharedPreferences The shared preferences to use for checking the token.
     */
    fun checkForFCMTokenUpdates(sharedPreferences: SharedPreferences) {
        myCloudMessagingToken.checkForFCMTokenUpdates(sharedPreferences)
    }

    /**
     * Signs out the user and navigates to the IntroActivity.
     *
     * @param activity The activity to use for starting the IntroActivity.
     */
    fun signOut(activity: Activity) {
        myCloudMessagingToken.clearFCMToken(PreferenceManager.getDefaultSharedPreferences(activity))
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }
}
