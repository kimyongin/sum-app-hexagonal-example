package com.example.port.inbound

import com.example.entity.Event

interface EventQueryPort {
    fun query(id: String): List<Event>
}