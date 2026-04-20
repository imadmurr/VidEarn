package murr.imad.myapplication.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.utils.Constants

/**
 * Repository class for handling user data in Firestore, using coroutines and Flow.
 */
class UserRepository {

    // Create an instance of Firebase Firestore
    private val fireStore = Firebase.firestore

    /**
     * Creates or updates a user's data in Firestore.
     *
     * @param userInfo The [User] object containing the user's data.
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend fun createUserData(userInfo: User): Result<Unit> {
        return try {
            fireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .set(userInfo, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reads the currently logged-in user's data from Firestore in real-time.
     *
     * This method listens for changes to the user's document and emits updates
     * whenever the data is modified.
     *
     * @return A [Flow] that emits [Result] objects with the latest [User] data or an exception.
     */
    suspend fun readUserDataFlow(): Flow<Result<User?>> = callbackFlow {
        val userDocRef = fireStore.collection(Constants.USERS).document(getCurrentUserID())

        // Add a snapshot listener to listen for real-time updates
        val listenerRegistration = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Emit failure result in case of an error
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Convert snapshot to User object and emit success result
                val user = snapshot.toObject(User::class.java)
                trySend(Result.success(user))
            } else {
                // If snapshot is null or doesn't exist, emit null user
                trySend(Result.success(null))
            }
        }

        // Clean up the listener when the flow is closed
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * Reads the currently logged-in user's data from Firestore once.
     *
     * This method retrieves the user's data without real-time updates.
     *
     * @return A [Result] object containing the [User] data or an exception.
     */
    suspend fun readUserData(): Result<User?> {
        return try {
            val userDocRef = fireStore.collection(Constants.USERS).document(getCurrentUserID())

            // Use Firebase's get() method to fetch the user's data
            val documentSnapshot = userDocRef.get().await()

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Convert snapshot to User object and return success result
                val user = documentSnapshot.toObject(User::class.java)
                Result.success(user)
            } else {
                // If snapshot is null or doesn't exist, return success with null user
                Result.success(null)
            }
        } catch (e: Exception) {
            // Return failure result in case of an exception
            Result.failure(e)
        }
    }



    /**
     * Updates specific fields of the user's data in Firestore.
     *
     * @param userHashMap A [HashMap] of fields to update.
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend fun updateUserData(userHashMap: HashMap<String, Any>): Result<Unit> {
        return try {
            fireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .update(userHashMap)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The current user ID or an empty string if not logged in.
     */
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }
}
