package vw

import GameJob
import GameState
import GameWorker
import Gelds
import data.GameRepository
import data.GameRepositoryImpl
import gelds
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
    val clock: Clock,
    private val gameRepository: GameRepository = GameRepositoryImpl()
) {
    private val _gameState: MutableStateFlow<GameState> = MutableStateFlow(gameRepository.getGame())
    val gameState: StateFlow<GameState> = _gameState

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMoney: StateFlow<Gelds> = clock.nowState
        .flatMapLatest { now ->
            gameState.map {
                it.currentMoney(now)
            }
        }
        .stateIn(scope, SharingStarted.Lazily, 0.gelds)

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
        _gameState.update { currentState ->
            var allEarned = 0L
            val updatedWorkers = currentState.workers.map { worker ->
                val jobForWorker = currentState.availableJobs.first { it.id == worker.jobId }
                val (done, earned) = worker.earnedWorker(
                    jobForWorker,
                    clock.now()
                )

                allEarned += earned
                worker.copy(createdAt = worker.createdAt + (done * jobForWorker.level.duration.raw))
            }
            currentState.copy(
                stashedMoney = currentState.stashedMoney + allEarned,
                workers = updatedWorkers
            ).also {
                println("Saved: $it")
                gameRepository.saveGame(it)
            }
        }
    }

    fun addWorker(job: GameJob) {
        save()
        _gameState.update { gameState ->
            if (gameState.stashedMoney >= job.level.cost) {
                gameState.copy(
                    workers = gameState.workers + GameWorker(job.id, clock.now()),
                    stashedMoney = gameState.stashedMoney - job.level.cost
                ).also {
                    gameRepository.saveGame(it)
                }
            } else {
                gameState
            }
        }
    }

    fun upgradeJob(gameJob: GameJob) {
        save()
        _gameState.update { gameState ->
            if (gameState.stashedMoney >= gameJob.level.cost) {
                gameState.copy(
                    availableJobs = gameState.availableJobs.map {
                        if (it == gameJob) gameJob.copy(level = gameJob.level.next()) else it
                    },
                    stashedMoney = gameState.stashedMoney - gameJob.level.cost
                )
            } else {
                gameState
            }.also {
                gameRepository.saveGame(it)
            }
        }
    }

    fun clickMoney() {
        _gameState.update {
            it.copy(stashedMoney = it.stashedMoney + 1)
        }
    }
}