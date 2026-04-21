package murr.imad.videarn.payouts.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.videarn.payouts.data.repository.PayoutsRepository
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.auth.data.repository.UserRepository
import murr.imad.videarn.payouts.data.model.CryptoPayout
import murr.imad.videarn.payouts.data.model.CryptoNetwork
import murr.imad.videarn.utils.Constants

/**
 * ViewModel for managing crypto payouts in the [CryptoPayoutActivity].
 * Handles user balance, supported networks, and payout processing.
 *
 * @property user The current user.
 */
class CryptoPayoutViewModel : ViewModel() {

    private val payoutsRepository = PayoutsRepository()
    private val userRepository = UserRepository()

    // LiveData for tracking the status of crypto payouts
    private val _cryptoPayoutStatus = MutableLiveData<Result<Unit>>()
    val cryptoPayoutStatus: LiveData<Result<Unit>> get() = _cryptoPayoutStatus

    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> get() = _userDetails

    // LiveData for loading the list of supported networks for a selected coin
    private val _networksList = MutableLiveData<List<CryptoNetwork>>()
    val networksList: LiveData<List<CryptoNetwork>> get() = _networksList

    init {
        loadUserData()
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
     * Loads the supported networks for a selected cryptocurrency.
     *
     * @param coin The selected cryptocurrency.
     */
    fun loadNetworksForCoin(coin: String) {
        val networks = when (coin) {
            "USDT" -> listOf("Polygon", "Tron")
            "BTC" -> listOf("Bitcoin")
            else -> emptyList()
        }
        _networksList.value = networks.map { CryptoNetwork(coin, it) }
    }

    /**
     * Validates the crypto payout form fields.
     *
     * @param amount The payout amount.
     * @param address The payout address.
     * @param coin The selected cryptocurrency.
     * @param network The selected network for the cryptocurrency.
     * @return True if all fields are valid, false otherwise.
     */
    private fun validateForm(amount: String, address: String, coin: String, network: String): Boolean {
        return amount.isNotEmpty() && validateAmount(coin, amount.toLong()) && validateAddress(network, address)
    }

    /**
     * Validates the payout amount against the user's available balance and minimum thresholds.
     *
     * @param coin The selected cryptocurrency.
     * @param amount The payout amount.
     * @return True if the amount is valid, false otherwise.
     */
    private fun validateAmount(coin: String, amount: Long): Boolean {
        val userPoints = userDetails.value?.points ?: return false

        return when (coin) {
            "USDT" -> amount in Constants.USDT_PAYMENT_THRESHOLD..userPoints
            "BTC" -> amount in Constants.BTC_PAYMENT_THRESHOLD..userPoints
            else -> false
        }
    }


    /**
     * Validates the payout address based on the selected network.
     *
     * @param network The selected network.
     * @param address The payout address.
     * @return True if the address is valid, false otherwise.
     */
    private fun validateAddress(network: String, address: String): Boolean {
        return when (network) {
            "Polygon" -> address.startsWith("0x") && address.length == 42
            "Tron" -> address.startsWith("T") && address.length == 34
            "Bitcoin" -> address.length <= 35
            else -> false
        }
    }

    /**
     * Initiates a crypto payout after validating the form.
     *
     * @param amount The payout amount.
     * @param address The payout address.
     * @param coin The selected cryptocurrency.
     * @param network The selected network.
     */
    fun addCryptoPayout(amount: String, address: String, coin: String, network: String) {
        if (validateForm(amount, address, coin, network)) {
            val payout = CryptoPayout(userRepository.getCurrentUserID(), amount.toLong(), address, coin, network)
            viewModelScope.launch {
                val result = userDetails.value?.let { payoutsRepository.addCryptoPayout(it, payout) }
                if (result?.isSuccess!!) {
                    updateUserPoints(-amount.toLong())
                } else {
                    _cryptoPayoutStatus.value = Result.failure(Exception("Failed to add payout"))
                }
            }
        } else {
            _cryptoPayoutStatus.value = Result.failure(Exception("Form validation failed"))
        }
    }

    /**
     * Updates the user's points after a successful payout.
     *
     * @param points The amount of points to deduct.
     */
    private fun updateUserPoints(points: Long) {
        viewModelScope.launch {
            val result = userDetails.value?.let { payoutsRepository.updateUserPoints(it, points) }
            _cryptoPayoutStatus.value = result!!
        }
    }
}
