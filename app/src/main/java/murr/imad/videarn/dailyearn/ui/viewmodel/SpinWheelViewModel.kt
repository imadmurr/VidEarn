package murr.imad.videarn.dailyearn.ui.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.auth.data.repository.UserRepository
import kotlin.random.Random
import rubikstudio.library.model.LuckyItem

/**
 * ViewModel for managing spin wheel logic.
 */
class SpinWheelViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    private val _coinAmount = MutableLiveData<Int>()
    val coinAmount: LiveData<Int> = _coinAmount

    private val data: MutableList<LuckyItem> = mutableListOf()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateUserStatus = MutableLiveData<Result<Unit>>()
    val updateUserStatus: LiveData<Result<Unit>> = _updateUserStatus


    init {
        setupSpinWheelData()
        fetchUserData()
    }

    /**
     * Fetches the current user's data from the repository
     * */
    private fun fetchUserData() {
        viewModelScope.launch {
            val result = userRepository.readUserData()
            result.onSuccess { _user.postValue(it) }
            result.onFailure { Log.e("HomeViewModel", "Error fetching user data", it) }
        }
    }

    /**
     * Updates the user's data and notifies the result.
     *
     * @param user The user whose points will be updated.
     * @param pointsUpdate The amount of points to add (positive value).
     */
    private fun updateUserData(user: User, pointsUpdate: Long) {
        viewModelScope.launch {
            // Get the current time in milliseconds
            val currentTimeMillis = System.currentTimeMillis()
            // Create a HashMap with the dailyBonusTime field
            val points = user.points + pointsUpdate
            val userHashMap = hashMapOf<String, Any>("dailyBonusTime" to currentTimeMillis,
                "points" to points)
            val result = userRepository.updateUserData(userHashMap)
            _updateUserStatus.value = result
        }
    }

    /**
     * Sets up the data for the spin wheel.
     */
    private fun setupSpinWheelData() {
        val luckyItem1 = LuckyItem().apply {
            text = "50"
            color = Color.parseColor("#B0E0E6")
        }
        data.add(luckyItem1)

        val luckyItem2 = LuckyItem().apply {
            text = "100"
            color = Color.parseColor("#6495ED")
        }
        data.add(luckyItem2)

        val luckyItem3 = LuckyItem().apply {
            text = "200"
            color = Color.parseColor("#4169E1")
        }
        data.add(luckyItem3)

        val luckyItem4 = LuckyItem().apply {
            text = "500"
            color = Color.parseColor("#0000CD")
        }
        data.add(luckyItem4)
    }

    /**
     * Returns a random index based on defined probabilities.
     * - Index 1: Least probable
     * - Index 2: Most probable
     * - Index 3: Intermediate probability
     * - Index 4: Least probable
     *
     * @return The randomly selected index.
     */
    fun getRandomIndex(): Int {
        val weights = mapOf(
            1 to 15, // Weight for index 1
            2 to 50, // Weight for index 2
            3 to 30, // Weight for index 3
            4 to 5   // Weight for index 4
        )

        val indices = mutableListOf<Int>()
        weights.forEach { (index, weight) ->
            repeat(weight) {
                indices.add(index)
            }
        }

        return indices[Random.nextInt(indices.size)]
    }

    /**
     * Sets the coin amount based on the selected index.
     *
     * @param index The index of the selected item.
     */
    fun updateCoinAmount(index: Int) {
        val amount = when (index) {
            1 -> 50
            2 -> 100
            3 -> 200
            4 -> 500
            else -> 0
        }
        _coinAmount.value = amount
        user.value?.let { coinAmount.value?.toLong()?.let { it1 -> updateUserData(it, it1) } }
    }

    /**
     * Generates a random round number for the spin wheel.
     *
     * @return The random round number.
     */
    fun getRandomRound(): Int {
        return Random.nextInt(10) + 15
    }

    /**
     * Provides the list of LuckyItems for the spin wheel.
     *
     * @return The list of LuckyItems.
     */
    fun getLuckyItems(): List<LuckyItem> {
        return data
    }
}
