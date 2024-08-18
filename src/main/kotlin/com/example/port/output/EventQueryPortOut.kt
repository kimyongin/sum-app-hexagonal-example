package com.example.port.output

import com.example.entity.Event

interface EventQueryPortOut {
    suspend fun query(id: String): List<Event>
}