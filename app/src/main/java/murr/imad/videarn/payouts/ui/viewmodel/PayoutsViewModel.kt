package murr.imad.videarn.payouts.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import murr.imad.videarn.auth.data.model.User

/**
 * [PayoutsViewModel] is responsible for managing UI-related data for the [PayoutsActivity].
 *
 * This ViewModel holds the user details that are passed from the [PayoutsActivity] and
 * exposes them through LiveData to observe changes.
 */
class PayoutsViewModel : ViewModel() {

    // Backing property for user details
    private val _userDetails = MutableLiveData<User>()
    // Exposed LiveData to be observed by the Activity
    val userDetails: LiveData<User> get() = _userDetails

    /**
     * Sets the user details in the ViewModel.
     *
     * This method updates the user details which can be observed by the UI components.
     *
     * @param user The [User] object containing user information.
     */
    fun setUserDetails(user: User) {
        _userDetails.value = user
    }
}
