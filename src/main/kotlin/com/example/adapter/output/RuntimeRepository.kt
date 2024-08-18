package com.example.adapter.output

import com.example.port.output.StatRuntimeQueryPortOut
import com.example.entity.StatRuntime

class RuntimeRepository : StatRuntimeQueryPortOut {
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