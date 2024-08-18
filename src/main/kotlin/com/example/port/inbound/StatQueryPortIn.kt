package com.example.port.inbound

import com.example.entity.Stat

interface StatQueryPortIn {
    suspend fun query(): Stat
}