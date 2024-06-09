package vw

import GameJob
import GameState
import GameWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import util.Clock
import util.GAME_TICK

internal class GameViewModel(
    val scope: CoroutineScope,
    val clock: Clock
) {
    private val _gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState.initial())
    val gameState: StateFlow<GameState> = _gameState

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMoney: StateFlow<Long> = clock.nowState
        .flatMapLatest { now ->
            gameState.map {
                it.currentMoney(now)
            }
        }
        .stateIn(scope, SharingStarted.Lazily, 0L)

    private var gameJob: Job? = null

    init {
        gameJob = (scope + Dispatchers.Unconfined + SupervisorJob()).launch {
            while (true) {
                delay(GAME_TICK)
                clock.tick()
                if (clock.now() % (50 * GAME_TICK) == 0L) {
                    save()
                }
            }
        }
    }

    fun clear() {
        gameJob?.cancel()
        scope.cancel()
    }

    fun save() {
        _gameState.update {
            var allEarned = 0L
            val updatedWorkers = it.workers.map { worker ->
                val (done, earned) = worker.earnedWorker(clock.now())

                allEarned += earned
                worker.copy(createdAt = worker.createdAt + (done * worker.job.duration.raw))
            }
            it.copy(stashedMoney = it.stashedMoney + allEarned, workers = updatedWorkers)
                .also {
                    println("Saved: $it")
                }
        }
    }

    fun addWorker(job: GameJob) {
        save()
        _gameState.update { gameState ->
            if (gameState.stashedMoney >= job.cost) {
                gameState.copy(
                    workers = gameState.workers + GameWorker(job, clock.now()),
                    stashedMoney = gameState.stashedMoney - job.cost
                )
            } else {
                gameState
            }
        }
    }

    fun clickMoney() {
        _gameState.update {
            it.copy(stashedMoney = it.stashedMoney + 1)
        }
    }
}