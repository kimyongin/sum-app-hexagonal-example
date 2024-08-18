package com.example.port.outbound

import com.example.entity.Event

interface EventHistoryQueryPort {
    suspend fun query(id: String): List<Event>
}