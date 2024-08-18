package com.example.port.input

import com.example.entity.Event

interface EventQueryPortIn {
    suspend fun query(id: String): List<Event>
}