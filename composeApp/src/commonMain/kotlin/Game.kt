import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.times
import kotlinx.serialization.Serializable
import util.Gelds
import util.GeldsSerializer
import util.gelds
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class GameState(
    @Serializable(with = GeldsSerializer::class)
    internal val stashedMoney: Gelds,
    val workers: List<GameWorker>,
    val availableJobs: List<GameJob> = listOf(
        GameJob(1, Level(1, 21.gelds, 1.gelds, 1.seconds)),
        GameJob(2, Level(1, 49.gelds, 2.gelds, 1.seconds)),
        GameJob(3, Level(1, 211.gelds, 17.gelds, 1.seconds)),
        GameJob(4, Level(1, 2008.gelds, 130.gelds, 1.seconds)),
        GameJob(5, Level(1, 13391.gelds, 643.gelds, 1.seconds))
    ),
)

@Serializable
data class GameWorker(
    val jobId: Int,
    val createdAt: Long,
) {

    fun earnedWorker(job: GameJob, now: Long): Pair<Long, Gelds> {
        val collected = abs((now - createdAt) / job.level.duration.inWholeMilliseconds)
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
    val duration: Duration,
) {
    fun upgradeEfficiency() = copy(
        level = level + 1,
        earn = earn.times(BigDecimal.fromFloat(1.9f)).roundToDigitPositionAfterDecimalPoint(0, roundingMode = RoundingMode.CEILING),
        cost = cost.times(BigDecimal.fromFloat(2.2f)).roundToDigitPositionAfterDecimalPoint(0, roundingMode = RoundingMode.FLOOR),
    )
}
