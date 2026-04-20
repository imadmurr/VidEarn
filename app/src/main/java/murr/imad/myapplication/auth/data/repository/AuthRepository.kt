package murr.imad.myapplication.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import murr.imad.myapplication.auth.data.model.User
import java.util.*

/**
 * Repository class for handling Firebase Authentication operations using coroutines.
 */
class AuthRepository {

    private val userRepository: UserRepository = UserRepository()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Signs in a user with email and password using Firebase Authentication.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return A [Result] indicating success or failure, with the authenticated [FirebaseUser] if successful.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User authentication failed")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs up a user with email and password and registers the user in Firestore.
     *
     * @param name The user's name.
     * @param email The user's email.
     * @param password The user's password.
     * @return A [Result] indicating success or failure, with the authenticated [FirebaseUser] if successful.
     */
    suspend fun signUp(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User registration failed")

            // Create a new user object to store in Firestore
            val inviteCode = UUID.randomUUID().toString().substring(0, 5)
            val newUser = User(
                id = firebaseUser.uid,
                name = name,
                email = firebaseUser.email ?: "",
                points = 0L, "",
                inviteCode = inviteCode
            )

            // Register user in Firestore
            userRepository.createUserData(newUser)

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Returns the currently authenticated user from Firebase.
     *
     * @return The current [FirebaseUser] or null if no user is authenticated.
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}
