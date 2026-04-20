package murr.imad.myapplication.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.payouts.data.repository.PayoutsRepository

/**
 * ViewModel for the Home screen, responsible for managing UI-related data
 * and business logic related to user payouts and data.
 */
class HomeViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()
    private val payoutsRepository = PayoutsRepository()

    private val _paidOutTotal = MutableLiveData<Double>()
    val paidOutTotal: LiveData<Double> = _paidOutTotal

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user


    init {
        fetchUserData()
    }


    /**
     * Fetches all payouts and calculates the total amount paid in both
     * cryptocurrency and gift cards.
     */
    fun fetchPayoutsAndCalculateTotals() {
        viewModelScope.launch {

            user.let { user ->
                val cryptoResult = user.value?.let { payoutsRepository.getAllCryptoPayouts(it) }
                val giftCardResult = user.value?.let { payoutsRepository.getAllGiftCardPayouts(it) }

                if (cryptoResult?.isSuccess == true && giftCardResult?.isSuccess == true) {
                    val cryptoTotalAmount = cryptoResult.getOrNull()
                        ?.filter { it.paid } // Filter only paid crypto payouts
                        ?.sumOf { it.amount } ?: 0.0

                    val giftCardTotalAmount = giftCardResult.getOrNull()
                        ?.filter { it.paid } // Filter only paid gift card payouts
                        ?.sumOf { it.ptsAmount!! } ?: 0.0

                    val total = cryptoTotalAmount.toDouble() + giftCardTotalAmount.toDouble()
                    _paidOutTotal.value = total
                } else {
                    // Handle errors
                    Log.e("HomeViewModel", "Error fetching payouts")
                }
            }
        }
    }

    /**
     * Fetches the current user's data from the repository.
     */
    private fun fetchUserData() {
        viewModelScope.launch {
            userRepository.readUserDataFlow().collect { result ->
                result.onSuccess { _user.postValue(it) }
                result.onFailure { Log.e("HomeViewModel", "Error fetching user data", it) }
            }
        }
    }
}
