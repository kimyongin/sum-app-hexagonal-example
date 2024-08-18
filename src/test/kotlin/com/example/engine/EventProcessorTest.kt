import com.example.engine.EventProcessor
import com.example.entity.Event
import com.example.port.outbound.EventQueryPortOut
import com.example.port.outbound.EventSavePortOut
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventProcessorTest {

    private lateinit var eventQueryPortOut: EventQueryPortOut
    private lateinit var eventSavePortOut: EventSavePortOut
    private lateinit var eventProcessor: EventProcessor

    @BeforeEach
    fun setUp() {
        eventQueryPortOut = mockk()
        eventSavePortOut = mockk()
        eventProcessor = EventProcessor(eventQueryPortOut, eventSavePortOut)
    }

    @Test
    fun `test buffer is flushed when buffer size limit is reached`() = runTest {
        val event1 = Event("1", 100L)
        val event2 = Event("2", 200L)
        val event3 = Event("3", 300L)

        coEvery { eventSavePortOut.saveBatch(any()) } returns Unit

        val deferreds = listOf(
            eventProcessor.save(event1),
            eventProcessor.save(event2),
            eventProcessor.save(event3)
        )
        deferreds.awaitAll()

        val eventListSlot = slot<List<Event>>()
        coVerify(exactly = 1) { eventSavePortOut.saveBatch(capture(eventListSlot)) }
        Assertions.assertEquals(setOf(event1, event2, event3), eventListSlot.captured.toSet())
    }


    @Test
    fun `test buffer is flushed after delay`() = runTest {
        val event1 = Event("1", 100L)
        val event2 = Event("2", 200L)

        coEvery { eventSavePortOut.saveBatch(any()) } returns Unit

        val deferreds = listOf(
            eventProcessor.save(event1),
            eventProcessor.save(event2),
        )
        deferreds.awaitAll()

        val eventListSlot = slot<List<Event>>()
        coVerify(exactly = 1) { eventSavePortOut.saveBatch(capture(eventListSlot)) }
        Assertions.assertEquals(setOf(event1, event2), eventListSlot.captured.toSet())
    }
}