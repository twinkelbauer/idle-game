package util

fun Gelds.toHumanReadableString(): String = when (exponent) {
    in 0..2 -> "${(significand)}"
    in 3..5 -> "${(significand / 1000)},${significand % 1000}k"
    in 6..8 -> "${(significand / 1000000)},${significand % 1000}M"
    in 9..11 -> "${(significand / 1000000000)},${significand % 1000}B"
    in 12..14 -> "${(significand / 1000000000000)},${significand % 1000}T"
    else -> "A lot"
}