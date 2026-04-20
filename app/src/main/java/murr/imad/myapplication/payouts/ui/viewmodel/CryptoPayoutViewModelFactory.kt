package murr.imad.myapplication.payouts.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import murr.imad.myapplication.auth.data.model.User

/**
 * Factory class for creating instances of [CryptoPayoutViewModel].
 * This factory ensures that the [CryptoPayoutViewModel] is created with the necessary
 * user argument.
 *
 * @property user The current user instance.
 */
class CryptoPayoutViewModelFactory(private val user: User) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A newly created [CryptoPayoutViewModel] instance.
     * @throws IllegalArgumentException if the provided [modelClass] is not assignable from [CryptoPayoutViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoPayoutViewModel::class.java)) {
            return CryptoPayoutViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
