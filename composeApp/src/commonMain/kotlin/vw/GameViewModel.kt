package vw

import GameJob
import GameState
import GameWorker
import data.GameRepository
import data.GameRepositoryImpl
import gelds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import util.GAME_TICK
import util.GameClock
import util.GameClockImpl

internal class GameViewModel(
    val scope: CoroutineScope,
    val gameClock: GameClock = GameClockImpl,
    private val gameRepository: GameRepository = GameRepositoryImpl()
) {
    val gameState: StateFlow<GameState> = gameRepository.getGame()
        .stateIn(scope, SharingStarted.Lazily, GameState(0, 0.gelds, emptyList()))

    private
    var gameJob: Job? = null

    init {
        gameJob = (scope + Dispatchers.Unconfined + SupervisorJob()).launch {
            while (true) {
                delay(GAME_TICK)
                gameClock.tick()
                gameRepository.saveGame(collectAllMoney(gameState.value))
            }
        }
    }

    fun clear() {
        gameJob?.cancel()
        scope.cancel()
    }

    fun addWorker(gameState: GameState, job: GameJob) {
        val updated = collectAllMoney(gameState)
        if (updated.stashedMoney < job.level.cost) return

        gameRepository.saveGame(
            updated.copy(
                workers = updated.workers + GameWorker(job.id, gameClock.now()),
                stashedMoney = updated.stashedMoney - job.level.cost
            )
        )
    }

    fun upgradeJob(gameState: GameState, gameJob: GameJob) {
        val updated = collectAllMoney(gameState)
        if (updated.stashedMoney < gameJob.level.cost) return

        gameRepository.saveGame(
            updated.copy(
                availableJobs = updated.availableJobs.map {
                    if (it == gameJob) {
                        gameJob.copy(level = gameJob.level.upgradeEfficiency())
                    } else {
                        it
                    }
                },
                stashedMoney = updated.stashedMoney - gameJob.level.cost
            )
        )
    }

    fun clickMoney(gameState: GameState) {
        gameRepository.saveGame(gameState.copy(stashedMoney = gameState.stashedMoney + 1.gelds))
    }

    private fun collectAllMoney(gameState: GameState): GameState {
        var allEarned = 0.gelds
        val updatedWorkers = gameState.workers.map { worker ->
            val jobForWorker = gameState.availableJobs.first { it.id == worker.jobId }
            val (done, earned) = worker.earnedWorker(jobForWorker, gameClock.now())
            allEarned += earned
            worker.copy(createdAt = worker.createdAt + (done * jobForWorker.level.duration.raw))
        }

        return gameState.copy(
            stashedMoney = gameState.stashedMoney + allEarned,
            workers = updatedWorkers,
        )
    }
}