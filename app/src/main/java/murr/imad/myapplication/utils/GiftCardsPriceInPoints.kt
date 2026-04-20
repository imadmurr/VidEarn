package murr.imad.myapplication.utils

object GiftCardsPriceInPoints {

    fun getCardPrice(type: String): List<Int>? {
        when (type) {
            "Steam Wallet" -> {
                return STEAM_REQUIRED_POINTS
            }
            "Amazon Card" -> {
                return AMAZON_REQUIRED_POINTS
            }
            "Netflix Card" -> {
                return NETFLIX_REQUIRED_POINTS
            }
            "Google Play" -> {
                return GOOGLE_PLAY_REQUIRED_POINTS
            }
        }
        return null
    }

    private val STEAM_REQUIRED_POINTS = listOf(
        60000,
        115000,
        220000,
        70000,
        120000,
        240000,
        75000,
        150000,
        13000,
        20000,
        35000,
        70000
    )

    private val AMAZON_REQUIRED_POINTS = listOf(
        13000,
        60000,
        115000,
        200000,
        75000,
        150000,
        13000,
        20000,
        35000,
        70000
    )

    private val NETFLIX_REQUIRED_POINTS = listOf(
        320000,
        300000,
    )

    private val GOOGLE_PLAY_REQUIRED_POINTS = listOf(
        60000,
        115000,
        70000,
        135000
    )
}