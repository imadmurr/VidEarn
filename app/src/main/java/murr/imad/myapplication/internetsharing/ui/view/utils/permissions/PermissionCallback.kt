package murr.imad.myapplication.internetsharing.ui.view.utils.permissions

/**
 * Callback interface to handle the result of permission requests.
 */
interface PermissionCallback {
    /**
     * Called when the permission is granted.
     */
    fun onPermissionGranted()

    /**
     * Called when the permission is denied.
     */
    fun onPermissionDenied()
    fun onPermissionGranted(permissionType: PermissionType)
}
