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
        GameJob(1, Level(1, 23.gelds, 1.gelds, 1.seconds)), //cost, earn, duration
        GameJob(2, Level(1, 49.gelds, 2.gelds, 1.seconds)),
        GameJob(3, Level(1, 211.gelds, 17.gelds, 1.seconds)),
        GameJob(4, Level(1, 2008.gelds, 130.gelds, 1.seconds)),
        GameJob(5, Level(1, 13391.gelds, 643.gelds, 1.seconds)),
        GameJob(6, Level(1, 202067.gelds, 10349.gelds, 1.seconds)),
        GameJob(7, Level(1, 2118939.gelds, 175933.gelds, 1.seconds)),
        GameJob(8, Level(1, 133492179.gelds, 7057572.gelds, 1.seconds)), //eq id5
        GameJob(9, Level(1, 2135874864.gelds, 119983294.gelds, 1.seconds)), //eq id6
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
