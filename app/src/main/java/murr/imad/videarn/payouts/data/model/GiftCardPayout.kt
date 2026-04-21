package murr.imad.videarn.payouts.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class GiftCardPayout(
    val id: String? ="",
    val ptsAmount: Int? = 0,
    val email: String? = "",
    val cardType: String? = "",
    val price: String? = "",
    val paid: Boolean = false,
    @ServerTimestamp
    val added_date: Date? = null
)
