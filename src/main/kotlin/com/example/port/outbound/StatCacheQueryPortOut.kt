package com.example.port.outbound

import com.example.entity.StatCache

interface StatCacheQueryPortOut {
    suspend fun query(): StatCache
}