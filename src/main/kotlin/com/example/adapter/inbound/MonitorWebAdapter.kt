package com.example.adapter.inbound

import com.example.port.inbound.StatQueryPort
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MonitorWebAdapter(private val statQueryPort: StatQueryPort) {

    private suspend fun getStats(call: ApplicationCall) {
        val stats = statQueryPort.query()
        call.respond(HttpStatusCode.OK, mapOf("stats" to stats))
    }

    fun setupRoutes(application: Application) {
        application.routing {
            get("/") {
                call.respondText("Hello, Ktor!")
            }

            get("/monitor/stats") {
                getStats(call)
            }
        }
    }
}