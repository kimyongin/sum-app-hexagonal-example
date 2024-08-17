package com.example

import com.example.adapter.inbound.EventWebAdapter
import com.example.adapter.inbound.MonitorWebAdapter
import com.example.adapter.outbound.EventHistoryRepository
import com.example.adapter.outbound.InterimResultRepository
import com.example.adapter.outbound.RuntimeRepository
import com.example.engine.EventProcessor
import com.example.engine.StatProcessor
import com.example.port.inbound.EventQueryPort
import com.example.port.inbound.EventSumPort
import com.example.port.inbound.StatQueryPort
import com.example.port.outbound.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    single { EventHistoryRepository() }
    single<EventHistoryQueryPort> { get<EventHistoryRepository>() }
    single<EventHistorySavePort> { get<EventHistoryRepository>() }

    single { InterimResultRepository() }
    single<InterimResultLoadPort> { get<InterimResultRepository>() }
    single<InterimResultSavePort> { get<InterimResultRepository>() }
    single<StatCacheQueryPort> { get<InterimResultRepository>() }

    single { EventProcessor(get(), get(), get(), get()) }
    single<EventSumPort> { get<EventProcessor>() }
    single<EventQueryPort> { get<EventProcessor>() }

    single { StatProcessor(get<StatCacheQueryPort>(), get<StatRuntimeQueryPort>()) }
    single<StatQueryPort> { get<StatProcessor>() }
    single<StatRuntimeQueryPort> { RuntimeRepository() }

    single { EventWebAdapter(get<EventSumPort>(), get<EventQueryPort>()) }
    single { MonitorWebAdapter(get<StatQueryPort>()) }
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    install(ContentNegotiation) {
        json()
    }

    configureRouting()
}

fun Application.configureRouting() {
    val eventWebAdapter by inject<EventWebAdapter>()
    val monitorWebAdapter by inject<MonitorWebAdapter>()

    eventWebAdapter.setupRoutes(this)
    monitorWebAdapter.setupRoutes(this)
}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}