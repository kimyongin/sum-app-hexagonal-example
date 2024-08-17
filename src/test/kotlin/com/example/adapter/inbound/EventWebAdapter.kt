package com.example.adapter.inbound

import com.example.entity.Event
import com.example.entity.EventResult
import com.example.port.inbound.EventSumPort
import com.example.port.inbound.EventQueryPort
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventWebAdapterTest {

    private val eventSumPort = mockk<EventSumPort>()
    private val eventQueryPort = mockk<EventQueryPort>()
    private lateinit var eventWebAdapter: EventWebAdapter

    @BeforeEach
    fun setup() {
        eventWebAdapter = EventWebAdapter(eventSumPort, eventQueryPort)
    }

    private fun Application.testSetup() {
        install(ContentNegotiation) {
            json()
        }
        eventWebAdapter.setupRoutes(this)
    }

    @Test
    fun `test sum route`() = testApplication {
        application {
            testSetup()
        }

        val event = Event("1", 100L)
        val eventResult = EventResult(200L)

        every { runBlocking { eventSumPort.sum(event.id, event.value) } } returns eventResult

        client.post("/event/sum") {
            contentType(ContentType.Application.Json)
            setBody("""{"id":"1","value":100}""")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("""{"newInterimResult":200}""", bodyAsText())
        }
    }

    @Test
    fun `test query route`() = testApplication {
        application {
            testSetup()
        }

        val id = "1"
        val eventHistory = listOf(100L, 200L, 300L)

        every { runBlocking { eventQueryPort.query(id) } } returns eventHistory

        client.get("/event/query?id=1") {
            contentType(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("""{"eventHistory":[100,200,300]}""", bodyAsText())
        }
    }
}