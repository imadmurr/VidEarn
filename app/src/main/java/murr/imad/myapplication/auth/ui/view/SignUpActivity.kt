package murr.imad.myapplication.auth.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import murr.imad.myapplication.R
import murr.imad.myapplication.auth.ui.viewmodel.SignUpViewModel
import murr.imad.myapplication.databinding.ActivitySignUpBinding
import murr.imad.myapplication.shared.ui.view.BaseActivity

/**
 * SignUpActivity handles the UI for the user registration process.
 * It interacts with SignUpViewModel to create user accounts via Firebase.
 */
class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding // View Binding instance
    private val signUpViewModel: SignUpViewModel by viewModels() // ViewModel instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        // Set up click listener for the sign-up button
        binding.signupButton.setOnClickListener {
            registerUser()
        }

        binding.loginAccount.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,
                SignInActivity::class.java))
            finish()
        }

        // Observe the sign-up result from the ViewModel and handle success or failure
        signUpViewModel.signUpResult.observe(this, Observer { result ->
            result.onSuccess { firebaseUser ->
                userRegisteredSuccess(firebaseUser)
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
        setSupportActionBar(binding.toolbarSignUpActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
    }*/

    /**
     * Validates the name, email, and password input fields, and triggers the sign-up process.
     * Shows a progress dialog while the sign-up is in progress.
     */
    private fun registerUser() {
        val name: String = binding.nameInput.text.toString().trim { it <= ' ' }
        val email: String = binding.emailInput.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordInput.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            showProgressDialog("Signing up...")
            signUpViewModel.signUp(name, email, password)
        }
    }

    /**
     * Validates the name, email, and password fields.
     *
     * @param name The name entered by the user.
     * @param email The email entered by the user.
     * @param password The password entered by the user.
     * @return True if all fields are valid, false otherwise.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
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
     * Handles the successful user registration.
     * Navigates the user to the EmailVerificationActivity after registration.
     *
     * @param firebaseUser The Firebase user who successfully registered.
     */
    private fun userRegisteredSuccess(firebaseUser: FirebaseUser) {
        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        startActivity(Intent(this@SignUpActivity, EmailVerificationActivity::class.java))
        finish()
    }
}
