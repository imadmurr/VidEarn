package murr.imad.myapplication.payouts.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.myapplication.payouts.data.repository.PayoutsRepository
import murr.imad.myapplication.payouts.data.model.CryptoPayout
import murr.imad.myapplication.payouts.data.model.GiftCardPayout
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.utils.Constants

class MyPayoutsViewModel : ViewModel() {

    private val payoutsRepository = PayoutsRepository()

    // LiveData to hold the list of cryptocurrency payouts
    private val _cryptoPayouts = MutableLiveData<List<CryptoPayout>>()
    val cryptoPayouts: LiveData<List<CryptoPayout>> get() = _cryptoPayouts

    // LiveData to hold the list of gift card payouts
    private val _giftCardPayouts = MutableLiveData<List<GiftCardPayout>>()
    val giftCardPayouts: LiveData<List<GiftCardPayout>> get() = _giftCardPayouts

    // LiveData to hold the result of the cancel operation
    private val _cancelResult = MutableLiveData<Result<Unit>>()
    val cancelResult: LiveData<Result<Unit>> get() = _cancelResult

    /**
     * Fetches payouts based on the selected option.
     *
     * @param option The type of payouts to fetch ("Cryptocurrency Payouts" or "Gift-Cards Payouts").
     * @param user The current user whose payouts are to be fetched.
     */
    fun fetchPayouts(option: String, user: User) {
        when (option) {
            "Cryptocurrency Payouts" -> fetchCryptoPayouts(user)
            "Gift-Cards Payouts" -> fetchGiftCardPayouts(user)
        }
    }

    /**
     * Fetches the list of cryptocurrency payouts for the user.
     *
     * @param user The current user whose payouts are to be fetched.
     */
    private fun fetchCryptoPayouts(user: User) {
        viewModelScope.launch {
            val result = payoutsRepository.getAllCryptoPayouts(user)
            result.onSuccess { _cryptoPayouts.postValue(it) }
                .onFailure { _cryptoPayouts.postValue(emptyList()) }
        }
    }

    /**
     * Fetches the list of gift card payouts for the user.
     *
     * @param user The current user whose payouts are to be fetched.
     */
    private fun fetchGiftCardPayouts(user: User) {
        viewModelScope.launch {
            val result = payoutsRepository.getAllGiftCardPayouts(user)
            result.onSuccess { _giftCardPayouts.postValue(it) }
                .onFailure { _giftCardPayouts.postValue(emptyList()) }
        }
    }

    /**
     * Cancels a payout based on its type and refunds points to the user.
     *
     * @param payout The payout to be cancelled, either a `CryptoPayout` or a `GiftCardPayout`.
     * @param user The current user making the request.
     * @param isCrypto Boolean indicating if the payout is a cryptocurrency payout.
     */
    fun cancelPayout(payout: Any, user: User, isCrypto: Boolean) {
        viewModelScope.launch {
            val result = if (isCrypto) {
                payoutsRepository.cancelCryptoPayout(user, payout as CryptoPayout)
            } else {
                payoutsRepository.cancelGiftCardPayout(user, payout as GiftCardPayout)
            }

            result.onSuccess {
                // Update user points after successful cancellation
                val pointsToRefund = calculatePointsToRefund(payout, isCrypto)
                pointsToRefund?.let { it1 -> updateUserPoints(user, it1) }
                _cancelResult.postValue(Result.success(Unit))
            }.onFailure {
                _cancelResult.postValue(Result.failure(it))
            }
        }
    }

    /**
     * Updates user points based on the type of payout cancelled.
     *
     * @param user The current user whose points need to be updated.
     * @param pointsUpdate The new points value to be updated.
     */
    private fun updateUserPoints(user: User, pointsUpdate: Long) {
        viewModelScope.launch {
            payoutsRepository.updateUserPoints(user, pointsUpdate)
        }
    }

    /**
     * Calculates the penalty points to be refunded to the user upon payout cancellation.
     *
     * @param payout The payout that was cancelled.
     * @param isCrypto Boolean indicating if the payout is a cryptocurrency payout.
     * @return The amount of points to be refunded.
     */
    private fun calculatePointsToRefund(payout: Any, isCrypto: Boolean): Long? {
        return if (isCrypto) {
            (payout as CryptoPayout).amount - Constants.PAYMENT_PENALTY
        } else {
            (payout as GiftCardPayout).ptsAmount?.minus(Constants.PAYMENT_PENALTY)
        }
    }
}

