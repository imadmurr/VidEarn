package murr.imad.myapplication.splashonboarding.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.auth.ui.view.EmailVerificationActivity
import murr.imad.myapplication.auth.ui.view.SignInActivity
import murr.imad.myapplication.databinding.ActivitySplashBinding
import murr.imad.myapplication.shared.ui.view.MainActivity

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        const val APP_UPDATE_REQUEST_CODE = 0
    }

    /**
     * Called when the activity is first created.
     * Sets up the splash screen, applies font styling, and navigates to the appropriate screen
     * based on the user's authentication and email verification status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Apply custom font to the app name text
        applyCustomFont()

        // Navigate to the appropriate activity after a 2-second delay
        navigateAfterDelay()
    }

    /**
     * Applies a custom font to the TextView that displays the app name.
     */
    private fun applyCustomFont() {
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typeface
    }

    /**
     * Handles navigation based on the user's authentication state.
     * If the user is authenticated but hasn't verified their email, directs them to EmailVerificationActivity.
     * If the user is authenticated and verified, directs them to MainActivity.
     * If the user is not authenticated, directs them to IntroActivity.
     */
    private fun navigateAfterDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserID = UserRepository().getCurrentUserID()
            val currentUser = FirebaseAuth.getInstance().currentUser

            when {
                currentUserID.isNotEmpty() && currentUser?.isEmailVerified == false -> {
                    startActivity(Intent(this, EmailVerificationActivity::class.java))
                }
                currentUserID.isNotEmpty() && currentUser?.isEmailVerified == true -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, SignInActivity::class.java))
                }
            }
            finish()
        }, 2000)
    }
}
