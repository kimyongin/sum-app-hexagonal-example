package com.example.port.input

import com.example.engine.EventProcessor
import com.example.entity.Event
import com.example.port.output.EventQueryPortOut
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventQueryPortInTest {

    private lateinit var eventQueryPortOut: EventQueryPortOut
    private lateinit var eventQueryPortIn: EventQueryPortIn

    @BeforeEach
    fun setUp() {
        // Mock 객체 초기화
        eventQueryPortOut = mockk()
        eventQueryPortIn = EventProcessor(eventQueryPortOut, mockk())
    }

    @Test
    fun `test queryEvent`() = runBlocking {
        // Given: 테스트에 필요한 데이터 설정
        val id = "user1"
        val eventHistory = listOf(
            Event("1", 100L),
            Event("1", 200L),
            Event("1", 300L)
        )
        coEvery { eventQueryPortOut.query(id) } returns eventHistory

        // When: 테스트 대상 메서드 호출
        val result = runBlocking { eventQueryPortIn.query(id) }

        // Then: 결과 검증
        coVerify { eventQueryPortOut.query(id) }
        assertEquals(eventHistory, result)
    }
}