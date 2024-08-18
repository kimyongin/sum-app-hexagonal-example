package com.example.port.outbound

import com.example.entity.Event

interface EventQueryPortOut {
    suspend fun query(id: String): List<Event>
}