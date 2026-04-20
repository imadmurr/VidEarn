package com.app.admin.data.model

sealed class Payout {
    data class Crypto(val payout: CryptoPayout) : Payout()
    data class GiftCard(val payout: GiftCardPayout) : Payout()
}
