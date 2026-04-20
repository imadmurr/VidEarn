package com.app.admin.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.admin.data.model.CryptoPayout
import com.app.admin.data.model.GiftCardPayout
import com.app.admin.data.repository.PayoutsRepository
import kotlinx.coroutines.launch

class PayoutsViewModel() : ViewModel() {

    private val repository: PayoutsRepository = PayoutsRepository()

    private val _cryptoPayouts = mutableStateOf(listOf<CryptoPayout>())
    val cryptoPayouts : State<List<CryptoPayout>> = _cryptoPayouts

    private val _giftCardPayouts = mutableStateOf(listOf<GiftCardPayout>())
    val giftCardPayouts : State<List<GiftCardPayout>> = _giftCardPayouts

    init {
        getAllCryptoPayouts()
        getAllGiftCardPayouts()
    }

    private fun getAllCryptoPayouts() {
        repository.getGiftCardPayouts(
            onSuccess = { payouts ->
                payouts?.forEach {
                    println("User: ${it.payout.id}, Amount: ${it.payout.ptsAmount}")
                }
            },
            onError = { error ->
                println("Error fetching gift card payouts: $error")
            }
        )
    }

    private fun getAllGiftCardPayouts() {
        viewModelScope.launch {
            //_giftCardPayouts.value = repository.getAllGiftCardPayouts().getOrDefault(emptyList())
        }
    }

}