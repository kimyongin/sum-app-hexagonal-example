package com.example.port.inbound

import com.example.entity.Event
import com.example.entity.Result

interface EventOperationPortIn {
    fun operation(interimResult: Result?, eventHistory: List<Event>, newEvent: Event): Result
}