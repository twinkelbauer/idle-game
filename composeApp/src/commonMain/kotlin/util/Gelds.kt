package util

import com.ionspin.kotlin.bignum.decimal.BigDecimal

typealias Gelds = BigDecimal

val Int.gelds: BigDecimal get() = BigDecimal.fromInt(this)
