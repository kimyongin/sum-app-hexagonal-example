package com.example.port.outbound

import com.example.entity.StatRuntime

interface StatRuntimeQueryPort {
    suspend fun query(): StatRuntime
}