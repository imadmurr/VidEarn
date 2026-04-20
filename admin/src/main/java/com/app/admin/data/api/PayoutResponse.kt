package com.app.admin.data.api

import com.app.admin.data.model.Payout

// Data class for the payout response
data class PayoutResponse(
    val success: Boolean,
    val data: List<Payout>? = null, // This will contain the list of payouts on success
    val error: String? = null       // This will contain an error message if something went wrong
)
