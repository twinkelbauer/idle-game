package vw

import GameJob
import GameState
import GameWorker
import data.GameRepository
import data.GameRepositoryImpl
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
import kotlinx.datetime.Clock
import util.gelds
import kotlin.time.Duration.Companion.seconds

internal class GameViewModel(
    val scope: CoroutineScope,
    private val gameClock: Clock = Clock.System,
    private val gameRepository: GameRepository = GameRepositoryImpl()
) {
    val gameState: StateFlow<GameState?> = gameRepository.getGame()
        .stateIn(scope, SharingStarted.Lazily, null)

    private var gameJob: Job? = null

    init {
        gameJob = (scope + Dispatchers.Unconfined + SupervisorJob()).launch {
            while (true) {
                delay(1.seconds)
                gameState.value
                    ?.let { collectAllMoney(it) }
                    ?.let { gameRepository.saveGame(it) }
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
                workers = updated.workers + GameWorker(
                    job.id,
                    gameClock.now().toEpochMilliseconds()
                ),
                stashedMoney = updated.stashedMoney - job.level.cost
            )
        )
    }

    fun upgradeJob(gameState: GameState, gameJob: GameJob) {
        val updated = collectAllMoney(gameState)
        if (updated.stashedMoney >= gameJob.level.cost) {
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
    }

    fun clickMoney(gameState: GameState) {
        gameRepository.saveGame(gameState.copy(stashedMoney = gameState.stashedMoney + 1.gelds))
    }

    fun reset() {
        gameRepository.saveGame(GameState(START_MONEY.gelds, emptyList()))
    }

    private fun collectAllMoney(gameState: GameState): GameState {
        var allEarned = 0.gelds
        val updatedWorkers = gameState.workers.map { worker ->
            val jobForWorker = gameState.availableJobs.first { it.id == worker.jobId }
            val (done, earned) = worker.earnedWorker(
                jobForWorker,
                gameClock.now().toEpochMilliseconds()
            )
            allEarned += earned
            worker.copy(
                createdAt = worker.createdAt.plus(
                    (done * jobForWorker.level.duration.inWholeMilliseconds)
                )
            )
        }

        return gameState.copy(
            stashedMoney = gameState.stashedMoney + allEarned,
            workers = updatedWorkers,
        )
    }
}

private const val START_MONEY = 0