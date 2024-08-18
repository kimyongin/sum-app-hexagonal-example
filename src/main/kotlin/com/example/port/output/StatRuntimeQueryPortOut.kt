package com.example.port.output

import com.example.entity.StatRuntime

interface StatRuntimeQueryPortOut {
    suspend fun query(): StatRuntime
}