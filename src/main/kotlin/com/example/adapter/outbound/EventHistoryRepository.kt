package com.example.adapter.outbound

import com.example.entity.Event
import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.EventHistorySavePort
import java.io.File

class EventHistoryRepository : EventHistoryQueryPort, EventHistorySavePort {
    private val eventStorage = mutableMapOf<String, MutableList<Long>>()
    private val storageDir = "user_events"

    // 이벤트 저장 (메모리와 파일에 저장)
    override fun save(event: Event) {
        eventStorage.computeIfAbsent(event.id) { mutableListOf() }.add(event.value)
        val file = File("$storageDir/${event.id}.txt")
        file.parentFile.mkdirs()
        file.appendText("${event.value}\n")
    }

    // 파일에서 이벤트 이력 불러오기
    override fun query(id: String): List<Event> {
        val file = File("$storageDir/$id.txt")
        if (!file.exists()) return emptyList()
        return file.readLines().map { Event(id, it.toLong()) }
    }
}
