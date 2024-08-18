package com.example.port.inbound

import com.example.entity.Event

interface EventQueryPortIn {
    suspend fun query(id: String): List<Event>
}