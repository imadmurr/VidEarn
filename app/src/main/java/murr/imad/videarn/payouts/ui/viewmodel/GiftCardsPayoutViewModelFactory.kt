package murr.imad.videarn.payouts.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import murr.imad.videarn.auth.data.model.User

/**
 * Factory class for creating instances of [GiftCardsPayoutViewModel].
 * This factory ensures that the [GiftCardsPayoutViewModel] is created with the necessary
 * user and gift card type arguments.
 *
 * @property user The current user instance.
 * @property giftCardType The type of the gift card selected by the user.
 */
class GiftCardsPayoutViewModelFactory(
    private val user: User,
    private val giftCardType: String
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A newly created [GiftCardsPayoutViewModel] instance.
     * @throws IllegalArgumentException if the provided [modelClass] is not assignable from [GiftCardsPayoutViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GiftCardsPayoutViewModel::class.java)) {
            return GiftCardsPayoutViewModel(user, giftCardType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
