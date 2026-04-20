package murr.imad.myapplication.payouts.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import murr.imad.myapplication.R
import murr.imad.myapplication.payouts.data.model.GiftCard

/**
 * ViewModel for managing the state of gift card data and navigation in the gift cards feature.
 */
class GiftCardsViewModel : ViewModel() {

    private val _giftCards = MutableLiveData<List<GiftCard>>()
    val giftCards: LiveData<List<GiftCard>> get() = _giftCards

    private val _selectedGiftCard = MutableLiveData<GiftCard?>()
    val selectedGiftCard: LiveData<GiftCard?> get() = _selectedGiftCard

    /**
     * Initializes the list of gift cards. This method should be called to load the data.
     */
    fun loadGiftCards() {
        val cards = listOf(
            GiftCard("Steam Wallet", R.drawable.steam),
            GiftCard("Amazon Card", R.drawable.amazon),
            GiftCard("Netflix Card", R.drawable.netflix),
            GiftCard("Google Play", R.drawable.googleplay)
        )
        _giftCards.value = cards
    }

    /**
     * Updates the selected gift card for navigation purposes.
     *
     * @param giftCard The gift card that was selected.
     */
    fun selectGiftCard(giftCard: GiftCard) {
        _selectedGiftCard.value = giftCard
    }

    /**
     * Clears the selected gift card, usually called after navigation has been handled.
     */
    fun clearSelectedGiftCard() {
        _selectedGiftCard.value = null
    }
}
