package util

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Serializable
data class Tick(
    val raw: Long
)

operator fun Tick.times(multiplier: Int): Tick = Tick(raw * multiplier)

operator fun Tick.div(divisor: Int): Tick = Tick(raw / divisor)
operator fun Tick.div(divisor: Float): Tick = Tick((raw / divisor).roundToLong())
