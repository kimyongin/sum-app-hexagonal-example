package com.example.port.outbound

import com.example.entity.Event

interface EventHistorySavePort {
    fun save(event: Event)
}