package com.example.adapter.inbound

import com.example.port.inbound.EventOperationPort
import com.example.port.inbound.EventQueryPort
import com.example.entity.Event
import com.example.entity.Result
import com.example.port.inbound.EventFilterPort
import com.example.port.inbound.EventSavePort
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.InterimResultLoadPort
import com.example.port.outbound.InterimResultSavePort
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope

class EventWebAdapter(
    private val eventFilterPort: EventFilterPort,
    private val eventSavePort: EventSavePort,
    private val eventQueryPort: EventQueryPort,
    private val eventOperationPort: EventOperationPort,
    private val eventHistoryQueryPort: EventHistoryQueryPort,
    private val interimResultLoadPort: InterimResultLoadPort,
    private val interimResultSavePort: InterimResultSavePort
) {

    suspend fun eventProcess(event: Event): Result? = coroutineScope {
        if (!eventFilterPort.filter(event)) {
            return@coroutineScope null
        }
        // 이벤트 저장
        eventSavePort.save(event).await()
        // 캐시에서 중간 집계값을 불러옴
        val interimResult = interimResultLoadPort.load(event.id)
        // 중간 집계값이 없으면 이벤트 이력을 불러옴
        val eventHistory = if (interimResult == null) {
            eventHistoryQueryPort.query(event.id)
        } else {
            emptyList()
        }
        // 이벤트를 처리하여 새로운 집계값을 계산
        val newResult = eventOperationPort.operation(interimResult, eventHistory, event)
        // 새로운 집계값을 캐시에 저장
        interimResultSavePort.save(event.id, newResult)
        // 새로운 집계값과 캐시 히트 여부 반환
        return@coroutineScope newResult
    }

    fun setupRoutes(application: Application) {
        application.routing {
            post("/event/process") {
                val event = call.receive<Event>()
                val result = eventProcess(event)
                call.respond(HttpStatusCode.OK, result ?: "")
            }

            get("/event/query") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
                val eventHistory = eventQueryPort.query(id)
                call.respond(HttpStatusCode.OK, eventHistory)
            }
        }
    }
}