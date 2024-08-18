import com.example.adapter.inbound.EventWebAdapter
import com.example.entity.Event
import com.example.entity.Result
import com.example.port.inbound.EventFilterPortIn
import com.example.port.inbound.EventOperationPortIn
import com.example.port.inbound.EventQueryPortIn
import com.example.port.inbound.EventSavePortIn
import com.example.port.outbound.EventQueryPortOut
import com.example.port.outbound.ResultLoadPortOut
import com.example.port.outbound.ResultSavePortOut
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventWebAdapterTest {

    private lateinit var eventFilterPortIn: EventFilterPortIn
    private lateinit var eventSavePortIn: EventSavePortIn
    private lateinit var eventQueryPortIn: EventQueryPortIn
    private lateinit var eventOperationPortIn: EventOperationPortIn
    private lateinit var eventQueryPortOut: EventQueryPortOut
    private lateinit var resultLoadPortOut: ResultLoadPortOut
    private lateinit var resultSavePortOut: ResultSavePortOut
    private lateinit var eventWebAdapter: EventWebAdapter

    @BeforeEach
    fun setUp() {
        eventFilterPortIn = mockk()
        eventSavePortIn = mockk()
        eventQueryPortIn = mockk()
        eventOperationPortIn = mockk()
        eventQueryPortOut = mockk()
        resultLoadPortOut = mockk()
        resultSavePortOut = mockk()
        eventWebAdapter = EventWebAdapter(
            eventFilterPortIn,
            eventSavePortIn,
            eventQueryPortIn,
            eventOperationPortIn,
            eventQueryPortOut,
            resultLoadPortOut,
            resultSavePortOut
        )
    }

    // 중간 결과가 null일 때 이벤트 히스토리가 조회되는지 테스트
    @Test
    fun `test event history is queried when interim result is null`() = runBlocking {
        val event = Event("1", 100L)
        val result = Result(200L)
        every { eventFilterPortIn.filter(event) } returns true
        coEvery { eventSavePortIn.save(event) } returns CompletableDeferred(Unit)
        every { resultLoadPortOut.load(event.id) } returns null // 중간결과 없음
        coEvery { eventQueryPortOut.query(event.id) } returns emptyList()
        every { eventOperationPortIn.operation(null, emptyList(), event) } returns result
        coEvery { resultSavePortOut.save(event.id, result) } returns Unit

        eventWebAdapter.eventProcess(event)

        coVerify(exactly = 1) { eventQueryPortOut.query(event.id) }
        coVerify(exactly = 1) { resultSavePortOut.save(event.id, result) }
    }

    // 중간 결과가 존재할 때 이벤트 히스토리를 조회하지 않는지 테스트
    @Test
    fun `test event history is not queried when interim result is not null`() = runBlocking {
        val event = Event("1", 100L)
        val interimResult = Result(150L)
        val result = Result(200L)
        every { eventFilterPortIn.filter(event) } returns true
        coEvery { eventSavePortIn.save(event) } returns CompletableDeferred(Unit)
        every { resultLoadPortOut.load(event.id) } returns interimResult // 중간결과 있음
        every { eventOperationPortIn.operation(interimResult, emptyList(), event) } returns result
        coEvery { resultSavePortOut.save(event.id, result) } returns Unit

        eventWebAdapter.eventProcess(event)

        coVerify(exactly = 0) { eventQueryPortOut.query(event.id) }
        coVerify(exactly = 1) { resultSavePortOut.save(event.id, result) }
    }
}