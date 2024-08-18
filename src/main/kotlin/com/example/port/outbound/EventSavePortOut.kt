package com.example.port.outbound

import com.example.entity.Event

interface EventSavePortOut {
    suspend fun saveBatch(events: List<Event>)
}