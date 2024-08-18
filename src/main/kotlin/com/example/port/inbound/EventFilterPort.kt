package com.example.port.inbound

import com.example.entity.Event

interface EventFilterPort {
    fun filter(event: Event): Boolean

}