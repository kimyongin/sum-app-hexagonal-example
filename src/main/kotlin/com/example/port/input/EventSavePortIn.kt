package com.example.port.input

import com.example.entity.Event
import kotlinx.coroutines.Deferred

interface EventSavePortIn {
    suspend fun save(event: Event): Deferred<Unit>
}