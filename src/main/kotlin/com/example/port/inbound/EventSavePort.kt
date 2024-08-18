package com.example.port.inbound

import com.example.entity.Event

interface EventSavePort {
    fun save(event: Event)

}