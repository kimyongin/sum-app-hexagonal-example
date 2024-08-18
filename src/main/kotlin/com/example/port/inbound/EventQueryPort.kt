package com.example.port.inbound

import com.example.entity.Event

interface EventQueryPort {
    suspend fun query(id: String): List<Event>
}