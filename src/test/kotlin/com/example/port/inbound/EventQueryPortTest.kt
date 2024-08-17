package com.example.port.inbound

import com.example.engine.EventProcessor
import com.example.port.outbound.EventHistoryQueryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventQueryPortTest {

    private lateinit var eventHistoryQueryPort: EventHistoryQueryPort
    private lateinit var eventQueryPort: EventQueryPort

    @BeforeEach
    fun setUp() {
        eventHistoryQueryPort = mockk()
        eventQueryPort = EventProcessor(eventHistoryQueryPort, mockk(), mockk(), mockk())
    }

    @Test
    fun `test queryEvent`() {
        // Given
        val id = "user1"
        val eventHistory = listOf(100L, 200L, 300L)

        every { eventHistoryQueryPort.query(id) } returns eventHistory

        // When
        val result = eventQueryPort.query(id)

        // Then
        verify { eventHistoryQueryPort.query(id) }
        assertEquals(eventHistory, result)
    }
}