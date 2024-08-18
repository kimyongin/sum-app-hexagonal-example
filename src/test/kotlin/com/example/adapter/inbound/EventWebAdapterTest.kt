import com.example.adapter.inbound.EventWebAdapter
import com.example.entity.Event
import com.example.entity.Result
import com.example.port.inbound.EventFilterPort
import com.example.port.inbound.EventOperationPort
import com.example.port.inbound.EventQueryPort
import com.example.port.inbound.EventSavePort
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.InterimResultLoadPort
import com.example.port.outbound.InterimResultSavePort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventWebAdapterTest {

    private lateinit var eventFilterPort: EventFilterPort
    private lateinit var eventSavePort: EventSavePort
    private lateinit var eventQueryPort: EventQueryPort
    private lateinit var eventOperationPort: EventOperationPort
    private lateinit var eventHistoryQueryPort: EventHistoryQueryPort
    private lateinit var interimResultLoadPort: InterimResultLoadPort
    private lateinit var interimResultSavePort: InterimResultSavePort
    private lateinit var eventWebAdapter: EventWebAdapter

    @BeforeEach
    fun setUp() {
        eventFilterPort = mockk()
        eventSavePort = mockk()
        eventQueryPort = mockk()
        eventOperationPort = mockk()
        eventHistoryQueryPort = mockk()
        interimResultLoadPort = mockk()
        interimResultSavePort = mockk()
        eventWebAdapter = EventWebAdapter(
            eventFilterPort,
            eventSavePort,
            eventQueryPort,
            eventOperationPort,
            eventHistoryQueryPort,
            interimResultLoadPort,
            interimResultSavePort
        )
    }

    // 중간 결과가 null일 때 이벤트 히스토리가 조회되는지 테스트
    @Test
    fun `test event history is queried when interim result is null`() = runBlocking {
        val event = Event("1", 100L)
        val result = Result(200L)
        every { eventFilterPort.filter(event) } returns true
        coEvery { eventSavePort.save(event) } returns CompletableDeferred(Unit)
        every { interimResultLoadPort.load(event.id) } returns null // 중간결과 없음
        coEvery { eventHistoryQueryPort.query(event.id) } returns emptyList()
        every { eventOperationPort.operation(null, emptyList(), event) } returns result
        coEvery { interimResultSavePort.save(event.id, result) } returns Unit

        eventWebAdapter.eventProcess(event)

        coVerify(exactly = 1) { eventHistoryQueryPort.query(event.id) }
        coVerify(exactly = 1) { interimResultSavePort.save(event.id, result) }
    }

    // 중간 결과가 존재할 때 이벤트 히스토리를 조회하지 않는지 테스트
    @Test
    fun `test event history is not queried when interim result is not null`() = runBlocking {
        val event = Event("1", 100L)
        val interimResult = Result(150L)
        val result = Result(200L)
        every { eventFilterPort.filter(event) } returns true
        coEvery { eventSavePort.save(event) } returns CompletableDeferred(Unit)
        every { interimResultLoadPort.load(event.id) } returns interimResult // 중간결과 있음
        every { eventOperationPort.operation(interimResult, emptyList(), event) } returns result
        coEvery { interimResultSavePort.save(event.id, result) } returns Unit

        eventWebAdapter.eventProcess(event)

        coVerify(exactly = 0) { eventHistoryQueryPort.query(event.id) }
        coVerify(exactly = 1) { interimResultSavePort.save(event.id, result) }
    }
}