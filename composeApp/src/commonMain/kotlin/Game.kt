import util.Tick
import kotlin.math.abs

abstract class GameObj(open val createdAt: Long)

data class GameState(
    internal val stashedMoney: Long,
    val workers: List<GameWorker>,
) {

    fun currentMoney(now: Long): Long = stashedMoney.plus(
        workers.sumOf { worker ->
            val (_, money) = worker.earnedWorker(now)
            money
        }
    )

    companion object {
        fun initial() = GameState(0, emptyList())
    }
}

data class GameWorker(
    val job: GameJob,
    override val createdAt: Long
) : GameObj(createdAt) {

    fun earnedWorker(now: Long): Pair<Long, Long> {
        val collected = abs((now - createdAt) / job.duration.raw)
        return collected to collected * job.earn
    }
}

data class GameJob(
    val earn: Int,
    val cost: Int,
    val duration: Tick,
)
