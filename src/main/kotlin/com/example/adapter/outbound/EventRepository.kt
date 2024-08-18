package com.example.adapter.outbound

import com.example.entity.Event
import com.example.port.outbound.EventQueryPortOut
import com.example.port.outbound.EventSavePortOut
import kotlinx.coroutines.*
import java.io.File

class EventRepository : EventQueryPortOut, EventSavePortOut {

    companion object {
        const val EVENTS_FILE_PATH = "user_events/events.txt"
    }
    private val eventStorage = mutableMapOf<String, MutableList<Long>>()

    override suspend fun saveBatch(events: List<Event>) {
        val fileContent = StringBuilder()
        events.forEach { event ->
            eventStorage.computeIfAbsent(event.id) { mutableListOf() }.add(event.value)
            fileContent.append("${event.id},${event.value}\n")
        }
        val file = File(EVENTS_FILE_PATH)
        file.parentFile?.mkdirs()
        file.appendText(fileContent.toString())
    }

    override suspend fun query(id: String): List<Event> {
        return withContext(Dispatchers.IO) {
            val file = File(EVENTS_FILE_PATH)
            if (!file.exists()) return@withContext emptyList()
            file.readLines()
                .map { it.split(",") }
                .filter { it[0] == id }
                .map { Event(it[0], it[1].toLong()) }
        }
    }
}