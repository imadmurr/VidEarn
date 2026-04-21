package murr.imad.videarn.splashonboarding.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import murr.imad.videarn.auth.ui.view.SignInActivity
import murr.imad.videarn.auth.ui.view.SignUpActivity
import murr.imad.videarn.databinding.ActivityIntroBinding

/**
 * IntroActivity displays the introductory screen for the app.
 * It provides options to navigate to the Sign-In or Sign-Up screens.
 */
class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    /**
     * Called when the activity is first created.
     * Sets up the introductory screen, applies custom font, and sets up click listeners
     * for navigating to Sign-In and Sign-Up screens.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Apply custom font to the app name text
        applyCustomFont()

        // Set up click listeners for buttons
        setupClickListeners()
    }

    /**
     * Applies a custom font to the TextView displaying the app name.
     */
    private fun applyCustomFont() {
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typeface
    }

    /**
     * Sets up click listeners for the Sign-In and Sign-Up buttons.
     * Navigates to the appropriate activities when buttons are clicked.
     */
    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
