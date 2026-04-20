package murr.imad.myapplication.auth.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import murr.imad.myapplication.R
import murr.imad.myapplication.databinding.ActivityEmailVerificationBinding
import murr.imad.myapplication.splashonboarding.ui.IntroActivity

class EmailVerificationActivity : AppCompatActivity() {

    // Declare the ViewBinding
    private lateinit var binding: ActivityEmailVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ViewBinding
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Send verification email when button is clicked
        binding.btnSendVerificationEmail.setOnClickListener {
            sendEmailVerification()
        }
    }

    private fun sendEmailVerification() {
        // Get instance of FirebaseAuth
        val firebaseAuth = FirebaseAuth.getInstance()
        // Get current user
        val firebaseUser = firebaseAuth.currentUser

        // Send email verification
        firebaseUser!!.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(
                    this@EmailVerificationActivity,
                    "Email Sent", Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@EmailVerificationActivity,
                    "Failed to send due to " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@EmailVerificationActivity, SignInActivity::class.java))
    }
}
