import com.example.engine.EventProcessor
import com.example.entity.Event
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.EventHistorySavePort
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

    private lateinit var eventHistoryQueryPort: EventHistoryQueryPort
    private lateinit var eventHistorySavePort: EventHistorySavePort
    private lateinit var eventProcessor: EventProcessor

    @BeforeEach
    fun setUp() {
        eventHistoryQueryPort = mockk()
        eventHistorySavePort = mockk()
        eventProcessor = EventProcessor(eventHistoryQueryPort, eventHistorySavePort)
    }

    @Test
    fun `test buffer is flushed when buffer size limit is reached`() = runTest {
        val event1 = Event("1", 100L)
        val event2 = Event("2", 200L)
        val event3 = Event("3", 300L)

        coEvery { eventHistorySavePort.saveBatch(any()) } returns Unit

        val deferreds = listOf(
            eventProcessor.save(event1),
            eventProcessor.save(event2),
            eventProcessor.save(event3)
        )
        deferreds.awaitAll()

        val eventListSlot = slot<List<Event>>()

        coVerify(exactly = 1) { eventHistorySavePort.saveBatch(capture(eventListSlot)) }

        // 순서와 상관없이 같은 요소를 가지고 있는지 검증
        Assertions.assertEquals(setOf(event1, event2, event3), eventListSlot.captured.toSet())
    }


    @Test
    fun `test buffer is flushed after delay`() = runTest {
        val event1 = Event("1", 100L)
        val event2 = Event("2", 200L)

        coEvery { eventHistorySavePort.saveBatch(any()) } returns Unit

        val deferreds = listOf(
            eventProcessor.save(event1),
            eventProcessor.save(event2),
        )
        deferreds.awaitAll()

        coVerify(exactly = 1) { eventHistorySavePort.saveBatch(listOf(event1, event2)) }
    }
}