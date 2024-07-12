import util.gelds
import util.times
import util.toHumanReadableString
import kotlin.test.Test
import kotlin.test.assertEquals

class LevelTest {

    @Test
    fun testHumanReadableString() {
        assertEquals("80", 80.gelds.toHumanReadableString())
        assertEquals("1k", 1_000.gelds.toHumanReadableString())
        assertEquals("50k", 50_000.gelds.toHumanReadableString())
        assertEquals("51k", 50_500.gelds.toHumanReadableString())
        assertEquals("999k", 999_000.gelds.toHumanReadableString())
        assertEquals("1M", 1_000_000.gelds.toHumanReadableString())
        assertEquals(
            expected = "117",
            actual = 500.gelds.times(0.234145F).toHumanReadableString(),
        )
        assertEquals(
            expected = "500",
            actual = 1000.gelds.times(0.5F).toHumanReadableString(),
        )
        assertEquals(
            expected = "5M",
            actual = 5_123_456.gelds.times(0.9876F).toHumanReadableString()
        )
    }
}