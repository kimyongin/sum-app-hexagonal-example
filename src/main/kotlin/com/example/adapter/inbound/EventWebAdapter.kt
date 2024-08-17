package com.example.adapter.inbound

import com.example.port.inbound.EventSumPort
import com.example.port.inbound.EventQueryPort
import com.example.entity.Event
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class EventWebAdapter(
    private val eventSumPort: EventSumPort,
    private val eventQueryPort: EventQueryPort
) {

    fun setupRoutes(application: Application) {
        application.routing {
            post("/event/sum") {
                val event = call.receive<Event>()
                val result = eventSumPort.sum(event.id, event.value)
                call.respond(HttpStatusCode.OK, mapOf("newInterimResult" to result.result))
            }

            get("/event/query") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
                val eventHistory = eventQueryPort.query(id)
                call.respond(HttpStatusCode.OK, mapOf("eventHistory" to eventHistory))
            }
        }
    }
}