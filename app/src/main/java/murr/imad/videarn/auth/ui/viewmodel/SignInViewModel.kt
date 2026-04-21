package murr.imad.videarn.auth.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import murr.imad.videarn.auth.data.repository.AuthRepository

/**
 * ViewModel for handling the sign-in logic.
 */
class SignInViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()

    private val _signInResult = MutableLiveData<Result<FirebaseUser>>()
    val signInResult: LiveData<Result<FirebaseUser>> = _signInResult

    /**
     * Attempts to sign in the user with the provided email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            _signInResult.value = result
        }
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The current [FirebaseUser] or null if no user is authenticated.
     */
    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}
