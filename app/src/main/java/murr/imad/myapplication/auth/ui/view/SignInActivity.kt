package murr.imad.myapplication.auth.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import murr.imad.myapplication.R
import murr.imad.myapplication.auth.ui.viewmodel.SignInViewModel
import murr.imad.myapplication.databinding.ActivitySignInBinding
import murr.imad.myapplication.shared.ui.view.BaseActivity
import murr.imad.myapplication.shared.ui.view.MainActivity

/**
 * SignInActivity handles the UI for the user sign-in process.
 * It interacts with SignInViewModel to authenticate users via Firebase.
 */
class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding // View Binding instance
    private val signInViewModel: SignInViewModel by viewModels() // ViewModel instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the activity full screen by hiding the status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // setupActionBar()

        var isPasswordVisible = false

        binding.eyeIcon.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.eyeIcon.setImageResource(R.drawable.ic_eye_closed)
            } else {
                // Show password
                binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.eyeIcon.setImageResource(R.drawable.ic_eye_open)
            }
            // Move the cursor to the end of the text
            binding.passwordInput.setSelection(binding.passwordInput.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, PasswordResetActivity::class.java)
            startActivity(intent)
        }


        // Set up click listener for the sign-in button
        binding.signinButton.setOnClickListener {
            signInUser()
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }

        // Observe the sign-in result from the ViewModel and handle success or failure
        signInViewModel.signInResult.observe(this, Observer { result ->
            result.onSuccess { firebaseUser ->
                handleSignInSuccess(firebaseUser)
            }.onFailure { exception ->
                hideProgressDialog()
                showErrorSnackBar(exception.message.toString())
            }
        })
    }

    /**
     * Set up the action bar with a back button.
     */
    /*private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }*/

    /**
     * Validates the email and password input fields, and triggers the sign-in process.
     * Shows a progress dialog while the sign-in is in progress.
     */
    private fun signInUser() {
        val email: String = binding.emailInput.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordInput.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog("Signing in...")
            signInViewModel.signIn(email, password)
        }
    }

    /**
     * Validates the email and password fields.
     *
     * @param email The email entered by the user.
     * @param password The password entered by the user.
     * @return True if both fields are valid, false otherwise.
     */
    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> true
        }
    }

    /**
     * Handles the successful sign-in.
     * Navigates the user to the MainActivity if the email is verified, otherwise prompts for verification.
     *
     * @param firebaseUser The Firebase user who successfully signed in.
     */
    private fun handleSignInSuccess(firebaseUser: FirebaseUser) {
        hideProgressDialog()

        if (firebaseUser.isEmailVerified) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        } else {
            showErrorSnackBar("Please verify account")
            startActivity(Intent(this@SignInActivity, EmailVerificationActivity::class.java))
            finish()
        }
    }
}
