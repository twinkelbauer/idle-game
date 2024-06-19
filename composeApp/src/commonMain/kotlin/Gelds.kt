import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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