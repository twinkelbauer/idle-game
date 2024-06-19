package util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GameClock {
    val nowState: StateFlow<Long>
    fun now(): Long

    fun tick()
}

const val GAME_TICK = 1000L

val Int.tick: Tick get() = Tick(this * GAME_TICK)
val Long.tick: Tick get() = Tick(this * GAME_TICK)

object GameClockImpl : GameClock {
    private val _nowState: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val nowState: StateFlow<Long> = _nowState.asStateFlow()

    override fun now() = _nowState.value

    override fun tick() {
        _nowState.update { it + GAME_TICK }
    }
}