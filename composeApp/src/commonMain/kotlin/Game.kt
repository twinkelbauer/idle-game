import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.Tick
import util.div
import util.tick
import kotlin.math.abs

typealias Gelds = BigDecimal

val Int.gelds: BigDecimal get() = BigDecimal.fromInt(this)
val Long.gelds: BigDecimal get() = BigDecimal.fromLong(this)

object GeldsSerializer : KSerializer<Gelds> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Gelds", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigDecimal =
        BigDecimal.parseString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString())
    }

}

@Serializable
data class GameState(
    @Serializable(with = GeldsSerializer::class)
    internal val stashedMoney: Gelds,
    val workers: List<GameWorker>,
    val availableJobs: List<GameJob> = listOf(
        GameJob(1, Level(1, 10.gelds, 1, 1.tick)),
        GameJob(2, Level(1, 50.gelds, 10, 80.tick)),
        GameJob(3, Level(1, 250.gelds, 50, 250.tick)),
        GameJob(4, Level(1, 500.gelds, 250, 500.tick)),
        GameJob(5, Level(1, 1000.gelds, 500, 1250.tick))
    ),
) {

    fun currentMoney(now: Long): Gelds = stashedMoney.plus(
        workers.sumOf { worker ->
            val job = availableJobs.first { it.id == worker.jobId }
            val (_, money) = worker.earnedWorker(job, now)
            money
        }
    )
}

@Serializable
data class GameWorker(
    val jobId: Int,
    val createdAt: Long,
) {

    fun earnedWorker(job: GameJob, now: Long): Pair<Long, Long> {
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
    val earn: Long,
    val duration: Tick,
) {
    fun next() = copy(
        level = level + 1,
        earn = earn * 2,
    )
}
