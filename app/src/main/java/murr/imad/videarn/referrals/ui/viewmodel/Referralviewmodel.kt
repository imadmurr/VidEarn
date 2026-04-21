package murr.imad.videarn.referrals.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.fcm.sendFcmNotification
import murr.imad.videarn.referrals.data.RedeemResult
import murr.imad.videarn.referrals.data.ReferralRepository

/**
 * UI state for the invite code redemption flow.
 */
sealed class RedeemUiState {
    object Idle : RedeemUiState()
    object Loading : RedeemUiState()
    object Success : RedeemUiState()
    object InvalidCode : RedeemUiState()
    object SelfReferral : RedeemUiState()
    data class Error(val message: String) : RedeemUiState()
}

/**
 * Owns the invite code redemption flow.
 *
 * Responsibilities:
 * - Calls [ReferralRepository] for data operations
 * - Sends the FCM notification to the referrer after a successful redemption
 * - Exposes [uiState] LiveData for the View to observe
 *
 * The View (Fragment/Activity) only observes state and calls [redeemInviteCode].
 * No business logic lives in the View.
 */
class ReferralViewModel : ViewModel() {

    private val repository = ReferralRepository()

    private val _uiState = MutableLiveData<RedeemUiState>(RedeemUiState.Idle)
    val uiState: LiveData<RedeemUiState> get() = _uiState

    /**
     * Attempts to redeem an invite code for the given user.
     *
     * On success, sends a push notification to the referrer and updates [uiState].
     *
     * @param inviteCode The code entered by the user.
     * @param currentUser The currently signed-in user.
     */
    fun redeemInviteCode(inviteCode: String, currentUser: User) {
        _uiState.value = RedeemUiState.Loading

        viewModelScope.launch {
            val result = repository.redeemInviteCode(inviteCode, currentUser)

            result.fold(
                onSuccess = { redeemResult ->
                    when (redeemResult) {
                        is RedeemResult.Success -> {
                            // Notify the referrer — fire-and-forget, don't block UI on it
                            sendFcmNotification(
                                recipientToken = redeemResult.referralUserFcmToken,
                                title = "New referral",
                                message = "${currentUser.name} joined using your invite code"
                            )
                            _uiState.postValue(RedeemUiState.Success)
                        }
                        RedeemResult.InvalidCode -> _uiState.postValue(RedeemUiState.InvalidCode)
                        RedeemResult.SelfReferral -> _uiState.postValue(RedeemUiState.SelfReferral)
                    }
                },
                onFailure = { e ->
                    _uiState.postValue(RedeemUiState.Error(e.message ?: "Unknown error"))
                }
            )
        }
    }
}