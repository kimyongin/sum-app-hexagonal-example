package com.example.port.inbound

import com.example.entity.Event

interface EventFilterPortIn {
    fun filter(event: Event): Boolean

}