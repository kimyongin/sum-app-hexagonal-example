package com.example.port.outbound

import com.example.entity.Event

interface EventHistorySavePort {
    suspend fun saveBatch(events: List<Event>)
}