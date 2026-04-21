package murr.imad.videarn.payouts.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.auth.data.repository.UserRepository
import murr.imad.videarn.payouts.data.model.GiftCardPayout
import murr.imad.videarn.payouts.data.repository.PayoutsRepository
import murr.imad.videarn.utils.GiftCardsPriceInPoints
import murr.imad.videarn.utils.GiftCardsPrices

/**
 * ViewModel for handling the gift card payout process.
 *
 * @property user The current user making the payout.
 * @property giftCardType The type of gift card selected for payout.
 */
class GiftCardsPayoutViewModel(
    private val user: User,
    private val giftCardType: String
) : ViewModel() {

    private val repository = PayoutsRepository()
    private val userRepository = UserRepository()

    private val _giftCardPriceOptions = MutableLiveData<List<String>>()
    val giftCardPriceOptions: LiveData<List<String>> get() = _giftCardPriceOptions

    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> get() = _userDetails

    private val _showError = MutableLiveData<String?>()
    val showError: LiveData<String?> get() = _showError

    private val _navigateToPayoutsActivity = MutableLiveData<Boolean>()
    val navigateToPayoutsActivity: LiveData<Boolean> get() = _navigateToPayoutsActivity

    private var selectedPricePosition = 0

    init {
        _giftCardPriceOptions.value = getPriceOptionsForGiftCard(giftCardType)
        loadUserData()
    }

    /**
     * Retrieves the available price options for the selected gift card type.
     *
     * @param giftCardType The type of gift card selected.
     * @return A list of price options for the selected gift card.
     */
    private fun getPriceOptionsForGiftCard(giftCardType: String): List<String> {
        return when (giftCardType) {
            "Steam Wallet" -> GiftCardsPrices.STEAM_GIFT_CARD_PRICES
            "Amazon Card" -> GiftCardsPrices.AMAZON_GIFT_CARD_PRICES
            "Netflix Card" -> GiftCardsPrices.NETFLIX_GIFT_CARD_PRICES
            "Google Play" -> GiftCardsPrices.GOOGLE_PLAY_GIFT_CARD_PRICES
            else -> emptyList()
        }
    }

    /**
     * Loads and sets the user's available balance in the UI.
     */
    private fun loadUserData() {
        viewModelScope.launch {
            userRepository.readUserDataFlow().collect { result ->
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _userDetails.postValue(user)
                } else {
                    _userDetails.postValue(null)
                }
            }
        }

    }

    /**
     * Sets the selected price position when a user selects a price option.
     *
     * @param position The position of the selected price.
     */
    fun onPriceSelected(position: Int) {
        selectedPricePosition = position
    }

    /**
     * Attempts to add a gift card payment after validating the form.
     *
     * @param email The email address to send the gift card to.
     */
    fun addGiftCardPayment(email: String) {
        if (validateForm(email)) {
            viewModelScope.launch {
                val price = GiftCardsPriceInPoints.getCardPrice(giftCardType)?.get(selectedPricePosition) ?: return@launch
                val payout = GiftCardPayout(
                    user.id,
                    price,
                    email,
                    giftCardType,
                    giftCardPriceOptions.value?.get(selectedPricePosition) ?: return@launch
                )

                val result = repository.addGiftCardPayout(user, payout)
                if (result.isSuccess) {
                    repository.updateUserPoints(user, -price.toLong())
                    _navigateToPayoutsActivity.value = true
                } else {
                    _showError.value = "Failed to add payment."
                }
            }
        }
    }

    /**
     * Validates the form data before proceeding with the payout.
     *
     * @param email The email address to validate.
     * @return `true` if the form is valid; `false` otherwise.
     */
    private fun validateForm(email: String): Boolean {
        return when {
            email.isBlank() -> {
                _showError.value = "Please enter email."
                false
            }
            (GiftCardsPriceInPoints.getCardPrice(giftCardType)?.get(selectedPricePosition)
                ?: 0) >= user.points -> {
                _showError.value = "Not enough points."
                false
            }
            else -> true
        }
    }

    /**
     * Resets the error message after it has been handled.
     */
    fun onErrorHandled() {
        _showError.value = null
    }
}
