package com.example.port.inbound

import com.example.engine.EventProcessor
import com.example.entity.EventResult
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.InterimResultLoadPort
import com.example.port.outbound.EventHistorySavePort
import com.example.port.outbound.InterimResultSavePort
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventSumPortTest {

    private lateinit var eventHistoryQueryPort: EventHistoryQueryPort
    private lateinit var eventHistorySavePort: EventHistorySavePort
    private lateinit var interimResultLoadPort: InterimResultLoadPort
    private lateinit var interimResultSavePort: InterimResultSavePort
    private lateinit var eventSumPort: EventSumPort

    @BeforeEach
    fun setUp() {
        eventHistoryQueryPort = mockk()
        eventHistorySavePort = mockk()
        interimResultLoadPort = mockk()
        interimResultSavePort = mockk()
        eventSumPort = EventProcessor(eventHistoryQueryPort, eventHistorySavePort, interimResultLoadPort, interimResultSavePort)
    }

    @Test
    fun `test sumEvent when cache is evicted`() {
        // Given
        val id = "user1"
        val eventValue = 1L
        val eventHistory = listOf(50L, 60L)
        val newCount = eventHistory.sum() + eventValue

        // 캐시에서 중간 집계값을 불러오지 못한 경우
        every { interimResultLoadPort.load(id) } returns null
        every { eventHistoryQueryPort.query(id) } returns eventHistory
        every { interimResultSavePort.save(id, newCount) } just Runs
        every { eventHistorySavePort.save(id, eventValue) } just Runs

        // When
        val result = eventSumPort.sum(id, eventValue)

        // Then
        // 이벤트 이력을 불러오는지 확인
        verify { eventHistoryQueryPort.query(id) }
        // 새로운 집계값을 캐시에 저장하는지 확인
        verify { interimResultSavePort.save(id, newCount) }
        // 이벤트를 저장하는지 확인
        verify { eventHistorySavePort.save(id, eventValue) }

        // 결과가 예상한 값과 일치하는지 확인
        assertEquals(EventResult(newCount), result)
    }

    @Test
    fun `test sumEvent when cache has interim result`() {
        // Given
        val id = "user1"
        val eventValue = 1L
        val interimResult = 110L
        val newCount = interimResult + eventValue

        // 캐시에서 중간 집계값을 불러온 경우
        every { interimResultLoadPort.load(id) } returns interimResult
        every { interimResultSavePort.save(id, newCount) } just Runs
        every { eventHistorySavePort.save(id, eventValue) } just Runs

        // When
        val result = eventSumPort.sum(id, eventValue)

        // Then
        // 이벤트 이력을 불러오지 않는지 확인
        verify(exactly = 0) { eventHistoryQueryPort.query(id) }
        // 새로운 집계값을 캐시에 저장하는지 확인
        verify { interimResultSavePort.save(id, newCount) }
        // 이벤트를 저장하는지 확인
        verify { eventHistorySavePort.save(id, eventValue) }

        // 결과가 예상한 값과 일치하는지 확인
        assertEquals(EventResult(newCount), result)
    }
}