package com.example.adapter.input

import com.example.entity.Event
import com.example.entity.Result
import com.example.port.input.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope

class EventWebAdapter(
    private val eventSavePortIn: EventSavePortIn,
    private val eventQueryPortIn: EventQueryPortIn,
    private val eventOperationPortIn: EventOperationPortIn,
    private val resultLoadPortIn: ResultLoadPortIn,
    private val resultSavePortIn: ResultSavePortIn
) {

    suspend fun eventProcess(event: Event): Result? = coroutineScope {
        // 이벤트 저장
        eventSavePortIn.save(event).await()
        // 캐시에서 중간 집계값을 불러옴
        val result = resultLoadPortIn.load(event.id)
        // 중간 집계값이 없으면 이벤트 이력을 불러옴
        val eventHistory = if (result == null) {
            eventQueryPortIn.query(event.id)
        } else {
            emptyList()
        }
        // 이벤트를 처리하여 새로운 집계값을 계산
        val newResult = eventOperationPortIn.operation(result, eventHistory, event)
        // 새로운 집계값을 캐시에 저장
        resultSavePortIn.save(event.id, newResult)
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
                val eventHistory = eventQueryPortIn.query(id)
                call.respond(HttpStatusCode.OK, eventHistory)
            }
        }
    }
}