package com.example

import com.example.adapter.input.EventWebAdapter
import com.example.adapter.input.MonitorWebAdapter
import com.example.adapter.output.EventRepository
import com.example.adapter.output.ResultRepository
import com.example.adapter.output.RuntimeRepository
import com.example.engine.EventProcessor
import com.example.engine.OperationProcessor
import com.example.engine.ResultProcessor
import com.example.engine.StatProcessor
import com.example.port.input.*
import com.example.port.output.*
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
    single { EventRepository() }
    single<EventQueryPortOut> { get<EventRepository>() }
    single<EventSavePortOut> { get<EventRepository>() }

    single { ResultRepository() }
    single<ResultLoadPortOut> { get<ResultRepository>() }
    single<ResultSavePortOut> { get<ResultRepository>() }
    single<StatCacheQueryPortOut> { get<ResultRepository>() }

    single { EventProcessor(get(), get()) }
    single<EventSavePortIn> { get<EventProcessor>() }
    single<EventQueryPortIn> { get<EventProcessor>() }

    single { ResultProcessor(get(), get()) }
    single<ResultSavePortIn> { get<ResultProcessor>() }
    single<ResultLoadPortIn> { get<ResultProcessor>() }

    single { OperationProcessor() }
    single<EventOperationPortIn> { get<OperationProcessor>() }

    single { StatProcessor(get<StatCacheQueryPortOut>(), get<StatRuntimeQueryPortOut>()) }
    single<StatQueryPortIn> { get<StatProcessor>() }
    single<StatRuntimeQueryPortOut> { RuntimeRepository() }

    single {
        EventWebAdapter(
            get<EventSavePortIn>(), get<EventQueryPortIn>(),
            get<EventOperationPortIn>(), get<ResultLoadPortIn>(), get<ResultSavePortIn>()
        )
    }
    single { MonitorWebAdapter(get<StatQueryPortIn>()) }
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