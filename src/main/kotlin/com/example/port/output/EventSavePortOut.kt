package com.example.port.output

import com.example.entity.Event

interface EventSavePortOut {
    suspend fun saveBatch(events: List<Event>)
}