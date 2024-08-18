package com.example.port.outbound

import com.example.entity.StatRuntime

interface StatRuntimeQueryPortOut {
    suspend fun query(): StatRuntime
}