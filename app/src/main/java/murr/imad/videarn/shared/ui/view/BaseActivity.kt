package murr.imad.videarn.shared.ui.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import murr.imad.videarn.R
import murr.imad.videarn.databinding.DialogProgressBinding

// TODO (Step 4: Here we have created a BaseActivity Class
//  in which we have added the progress dialog and SnackBar.
//  Now all the activity will extend the BaseActivity instead of AppCompatActivity.)
open class BaseActivity : AppCompatActivity() {

    private lateinit var progressDialog: Dialog
    private lateinit var progressBinding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)

        // Inflate the dialog layout with view binding
        progressBinding = DialogProgressBinding.inflate(layoutInflater)
        progressDialog.setContentView(progressBinding.root)
        progressDialog.setCancelable(false)

        // Set the text for the TextView using the binding
        progressBinding.tvProgressText.text = text

        // Start the dialog and display it on screen.
        progressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    /**
     * This function is used to double check if a user wants to exit the app
     */
    fun doubleBackToExit() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Do you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    /**
     * This function is used to show an error snack-bar for user
     */
    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }
}
