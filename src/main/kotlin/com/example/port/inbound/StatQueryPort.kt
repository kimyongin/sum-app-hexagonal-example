package com.example.port.inbound

import com.example.entity.Stat

interface StatQueryPort {
    suspend fun query(): Stat
}