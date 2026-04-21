package murr.imad.videarn.dailyearn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.auth.data.repository.UserRepository

class DailyEarnViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        // Fetch user data on ViewModel initialization
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val result = userRepository.readUserData()
            result.onSuccess { _user.postValue(it) }
            result.onFailure {
                Log.e("DailyViewModel", "Error fetching user data", it)
            }
        }
    }
}