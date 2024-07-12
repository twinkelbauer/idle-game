package util


fun Gelds.toHumanReadableString(): String {
    val float = this.floatValue(exactRequired = false)

    return when (exponent) {
        in 0..2 -> "$float"
        in 3..5 -> "${float / 1_000}"
        in 6..8 -> "${float / 1_000_000}"
        in 9..11 -> "${float / 1_000_000_000}"
        in 12..14 -> "${float / 1_000_000_000_000}"
        else -> "$float"
    }
        .split(".")
        .let { (integer, decimal) ->
            integer.plus(
                if (decimal != "0") {
                    ",${decimal.take(2).padEnd(2, '0')}"
                } else {
                    ""
                }
            )
        }
        .plus(
            when (exponent) {
                in 0..2 -> ""
                in 3..5 -> "k"
                in 6..8 -> "M"
                in 9..11 -> "B"
                in 12..14 -> "T"
                else -> "A lot"
            }
        )
}