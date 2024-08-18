package com.example.port.input

import com.example.entity.Event
import com.example.entity.Result

interface EventOperationPortIn {
    fun operation(result: Result?, eventHistory: List<Event>, newEvent: Event): Result
}