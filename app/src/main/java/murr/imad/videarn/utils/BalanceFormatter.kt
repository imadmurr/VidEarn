package murr.imad.videarn.utils

/**
 * Converts user points (Int) to dollars.
 * 10,000 points = 1.000 dollars.
 *
 * @return The dollar value as a formatted string with 3 decimal places.
 */
fun Int.toDollars(): String {
    val dollars = this / 10000.0
    return String.format("$%.3f", dollars)
}

/**
 * Converts nullable user points (Int?) to dollars.
 * 10,000 points = 1.000 dollars.
 * If points are null, returns "0.000 $".
 *
 * @return The dollar value as a formatted string with 3 decimal places.
 */
fun Int?.toDollars(): String {
    val dollars = (this ?: 0) / 10000.0
    return String.format("%.3f $", dollars)
}

/**
 * Converts user points (Long) to dollars.
 * 10,000 points = 1.000 dollars.
 *
 * @return The dollar value as a formatted string with 3 decimal places.
 */
fun Long.toDollars(): String {
    val dollars = this / 10000.0
    return String.format("$%.3f", dollars)
}

/**
 * Converts nullable user points (Long?) to dollars.
 * 10,000 points = 1.000 dollars.
 * If points are null, returns "0.000 $".
 *
 * @return The dollar value as a formatted string with 3 decimal places.
 */
fun Long?.toDollars(): String {
    val dollars = (this ?: 0L) / 10000.0
    return String.format("%.3f $", dollars)
}

/**
 * Converts user points (Double) to dollars.
 * Assumes points are represented as a double value.
 *
 * @return The dollar value as a formatted string with 3 decimal places.
 */
fun Double.toDollars(): String {
    val dollars = this / 10000.0
    return String.format("$%.3f", dollars)
}

/**
 * Converts nullable user points (Double?) to dollars.
 * If points are null, returns "0.000 $".
 *
 * @return The dollar value as a formatted string with 3 decimal places.
 */
fun Double?.toDollars(): String {
    val dollars = (this ?: 0.0) / 10000.0
    return String.format("%.3f $", dollars)
}
