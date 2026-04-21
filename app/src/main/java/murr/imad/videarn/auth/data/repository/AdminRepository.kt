package murr.imad.videarn.auth.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import murr.imad.videarn.auth.data.model.Admin
import murr.imad.videarn.utils.Constants

/**
 * Repository for admin-related Firestore queries.
 *
 * This class is a pure data layer — notification dispatch is handled by the ViewModel
 * using [sendFcmNotification] from FcmNotificationSender.kt.
 */
class AdminRepository {

    companion object {
        private const val ADMIN_ID = "uvNDnXe6CYQXw2Dw6IfvnylUzkv1"
    }

    /**
     * Returns the admin's FCM token, or null if not found.
     *
     * The caller (ViewModel) is responsible for sending the notification
     * using the token returned here.
     */
    suspend fun getAdminFcmToken(): String? {
        return try {
            val snapshot = Firebase.firestore
                .collection(Constants.USERS)
                .whereEqualTo(Constants.FIELD_ID, ADMIN_ID)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.toObject<Admin>()?.fcmToken
        } catch (e: Exception) {
            null
        }
    }
}