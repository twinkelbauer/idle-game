package util

interface Tick {
    val raw: Long
}

data class TickImpl(override val raw: Long) : Tick