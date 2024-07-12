package util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

typealias Gelds = BigDecimal

val Int.gelds: BigDecimal get() = BigDecimal.fromInt(this)

operator fun Gelds.times(other: Float) = this.times(BigDecimal.fromFloat(other))
