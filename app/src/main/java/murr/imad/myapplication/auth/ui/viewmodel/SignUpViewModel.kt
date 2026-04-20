package murr.imad.myapplication.auth.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import murr.imad.myapplication.auth.data.repository.AuthRepository

/**
 * ViewModel for handling the sign-up logic.
 */
class SignUpViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()

    private val _signUpResult = MutableLiveData<Result<FirebaseUser>>()
    val signUpResult: LiveData<Result<FirebaseUser>> = _signUpResult

    /**
     * Attempts to sign up the user with the provided name, email, and password.
     *
     * @param name The user's name.
     * @param email The user's email.
     * @param password The user's password.
     */
    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.signUp(name, email, password)
            _signUpResult.value = result
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
