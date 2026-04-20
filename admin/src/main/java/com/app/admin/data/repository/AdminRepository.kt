package com.app.admin.data.repository

import com.app.admin.data.model.Admin
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Repository class for managing admin-related data from Firestore and
 * sending notifications via FCM.
 */
class AdminRepository {

    companion object {
        private const val ADMIN_ID = "uvNDnXe6CYQXw2Dw6IfvnylUzkv1"
    }

    /**
     * Fetches a [QuerySnapshot] containing the admin document from the "users" collection.
     *
     * @return The [QuerySnapshot] if the admin is found, or null if there is an exception or no result.
     */
    private suspend fun getAdmin(): QuerySnapshot? {
        return try {
            val firestore = Firebase.firestore
            firestore.collection("users")
                .whereEqualTo("id", ADMIN_ID)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extracts the admin information from the [QuerySnapshot] and maps it to an [Admin] object.
     *
     * @return The [Admin] object if found, or null if not found or an error occurs.
     */
    private suspend fun getAdminUser(): Admin? {
        return try {
            val querySnapshot = getAdmin()
            querySnapshot?.documents?.firstOrNull()?.toObject<Admin>()
        } catch (e: Exception) {
            null
        }
    }
}
