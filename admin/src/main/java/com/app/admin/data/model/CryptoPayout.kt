package com.app.admin.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class CryptoPayout(
    val id: String? ="",
    val amount: Long = 0L,
    val address: String? = "",
    val coin: String? = "",
    val network: String? = "",
    val paid: Boolean = false,
    @ServerTimestamp
    val added_date: Date? = null
)