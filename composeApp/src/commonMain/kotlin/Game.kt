import com.ionspin.kotlin.bignum.decimal.times
import kotlinx.serialization.Serializable
import util.Tick
import util.tick
import kotlin.math.abs

@Serializable
data class GameState(
    val savedAt: Long,
    @Serializable(with = GeldsSerializer::class)
    internal val stashedMoney: Gelds,
    val workers: List<GameWorker>,
    val availableJobs: List<GameJob> = listOf(
        GameJob(1, Level(1, 10.gelds, 1.gelds, 1.tick)),
        GameJob(2, Level(1, 50.gelds, 10.gelds, 80.tick)),
        GameJob(3, Level(1, 250.gelds, 50.gelds, 250.tick)),
        GameJob(4, Level(1, 500.gelds, 250.gelds, 500.tick)),
        GameJob(5, Level(1, 1000.gelds, 500.gelds, 1250.tick))
    ),
)

@Serializable
data class GameWorker(
    val jobId: Int,
    val createdAt: Long,
) {

    fun earnedWorker(job: GameJob, now: Long): Pair<Long, Gelds> {
        val collected = abs((now - createdAt) / job.level.duration.raw)
        return collected to collected * job.level.earn
    }
}

@Serializable
data class GameJob(
    val id: Int,
    val level: Level,
)

@Serializable
data class Level(
    val level: Int,
    @Serializable(with = GeldsSerializer::class)
    val cost: Gelds,
    @Serializable(with = GeldsSerializer::class)
    val earn: Gelds,
    val duration: Tick,
) {
    fun upgradeEfficiency() = copy(
        level = level + 1,
        earn = earn * 2,
    )
}
