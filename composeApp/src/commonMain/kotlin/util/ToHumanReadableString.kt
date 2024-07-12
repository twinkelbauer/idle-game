package util

import com.ionspin.kotlin.bignum.decimal.RoundingMode

fun Gelds.toHumanReadableString(): String =
    "${
        this.roundToDigitPosition(
            1 + exponent % 3,
            RoundingMode.ROUND_HALF_CEILING
        ).significand
    }".plus(
        when (exponent) {
            in 0..2 -> ""
            in 3..5 -> "k"
            in 6..8 -> "M"
            in 9..11 -> "B"
            in 12..14 -> "T"
            else -> "A lot"
        }
    )