package com.example.adapter.outbound

import com.example.port.outbound.StatRuntimeQueryPort
import com.example.entity.StatRuntime

class RuntimeRepository : StatRuntimeQueryPort {
    override suspend fun query(): StatRuntime {
        val totalMemory = Runtime.getRuntime().totalMemory()
        val freeMemory = Runtime.getRuntime().freeMemory()
        val usedMemory = totalMemory - freeMemory
        val maxMemory = Runtime.getRuntime().maxMemory()

        return StatRuntime(
            totalMemory = totalMemory,
            freeMemory = freeMemory,
            usedMemory = usedMemory,
            maxMemory = maxMemory
        )
    }
}