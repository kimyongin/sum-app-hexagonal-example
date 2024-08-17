package com.example.adapter.outbound

import com.example.port.outbound.EventHistoryQueryPort
import com.example.port.outbound.EventHistorySavePort
import java.io.File

class EventHistoryRepository : EventHistoryQueryPort, EventHistorySavePort {
    private val eventStorage = mutableMapOf<String, MutableList<Long>>()
    private val storageDir = "user_events"

    // 이벤트 저장 (메모리와 파일에 저장)
    override fun save(id: String, value: Long) {
        eventStorage.computeIfAbsent(id) { mutableListOf() }.add(value)
        val file = File("$storageDir/$id.txt")
        file.parentFile.mkdirs()
        file.appendText("$value\n")
    }

    // 파일에서 이벤트 이력 불러오기
    override fun query(id: String): List<Long> {
        val file = File("$storageDir/$id.txt")
        if (!file.exists()) return emptyList()
        return file.readLines().mapNotNull { it.toLongOrNull() }
    }
}
