package com.example.port.inbound

import com.example.entity.Event
import kotlinx.coroutines.Deferred

interface EventSavePortIn {
    suspend fun save(event: Event): Deferred<Unit>
}