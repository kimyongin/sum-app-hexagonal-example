package com.example.adapter.input

import com.example.port.input.StatQueryPortIn
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MonitorWebAdapter(private val statQueryPortIn: StatQueryPortIn) {

    private suspend fun getStats(call: ApplicationCall) {
        val stats = statQueryPortIn.query()
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