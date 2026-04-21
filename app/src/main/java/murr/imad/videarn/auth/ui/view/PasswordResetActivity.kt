package murr.imad.videarn.auth.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import murr.imad.videarn.databinding.ActivityPasswordResetBinding

class PasswordResetActivity : AppCompatActivity() {

    // Declare the ViewBinding
    private lateinit var binding: ActivityPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ViewBinding
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the reset password button click listener
        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailInput.text.toString()

            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
