package murr.imad.videarn.internetsharing.ui.view.utils.permissions

import android.Manifest
import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity

/**
 * Util class for checking and requesting permissions related to data usage tracking.
 */
object PermissionUtil {

    /**
     * Request code for READ_PHONE_STATE permission.
     */
    const val READ_PHONE_STATE_REQUEST_CODE = 101

    /**
     * Request code for PACKAGE_USAGE_STATS permission.
     */
    const val PACKAGE_USAGE_STATS_REQUEST_CODE = 102

    /**
     * Checks and requests READ_PHONE_STATE permission.
     *
     * @param activity The [AppCompatActivity] requesting the permission.
     * @param callback The callback to handle the result of the permission request.
     */
    fun checkAndRequestReadPhoneStatePermission(
        activity: FragmentActivity,
        callback: PermissionCallback
    ) {
        if (isReadPhoneStatePermissionGranted(activity)) {
            callback.onPermissionGranted()
        } else {
            requestReadPhoneStatePermission(activity, callback)
        }
    }

    /**
     * Checks and requests PACKAGE_USAGE_STATS permission.
     *
     * @param activity The [AppCompatActivity] requesting the permission.
     * @param callback The callback to handle the result of the permission request.
     */
    fun checkAndRequestPackageUsageStatsPermission(
        activity: FragmentActivity,
        callback: PermissionCallback
    ) {
        if (isPackageUsageStatsPermissionGranted(activity)) {
            callback.onPermissionGranted()
        } else {
            requestPackageUsageStatsPermission(activity, callback)
        }
    }

    /**
     * Checks if READ_PHONE_STATE permission is granted.
     *
     * @param context The [Context] to check the permission.
     * @return `true` if the permission is granted, `false` otherwise.
     */
    fun isReadPhoneStatePermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if PACKAGE_USAGE_STATS permission is granted.
     *
     * @param context The [Context] to check the permission.
     * @return `true` if the permission is granted, `false` otherwise.
     */
    fun isPackageUsageStatsPermissionGranted(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Requests READ_PHONE_STATE permission.
     *
     * @param activity The [AppCompatActivity] requesting the permission.
     * @param callback The callback to handle the result of the permission request.
     */
    private fun requestReadPhoneStatePermission(
        activity: FragmentActivity,
        callback: PermissionCallback
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            READ_PHONE_STATE_REQUEST_CODE
        )
    }

    /**
     * Requests PACKAGE_USAGE_STATS permission.
     *
     * @param activity The [AppCompatActivity] requesting the permission.
     * @param callback The callback to handle the result of the permission request.
     */
    private fun requestPackageUsageStatsPermission(
        activity: FragmentActivity,
        callback: PermissionCallback
    ) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivityForResult(intent, PACKAGE_USAGE_STATS_REQUEST_CODE)
    }

    /**
     * Shows an [AlertDialog] to request the specified permission.
     *
     * @param context The [Context] to show the alert dialog.
     * @param message The message explaining the need for the permission.
     * @param positiveButtonText The text for the positive button.
     * @param negativeButtonText The text for the negative button.
     * @param positiveButtonAction The action to be performed when the positive button is clicked.
     * @param negativeButtonAction The action to be performed when the negative button is clicked.
     */
    fun showPermissionAlertDialog(
        context: Context,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveButtonAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ -> positiveButtonAction.invoke() }
            .setNegativeButton(negativeButtonText) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    /**
     * Prompt user to revoke permission
     *
     * @param context
     */
    fun promptUserToRevokePermission(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Permission Needed")
            .setMessage("To disable this permission please go to Permissions -> Phone -> Don't Allow")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings(context)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Open app settings
     *
     * @param context
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        startActivity(context,intent,null)
    }


}
