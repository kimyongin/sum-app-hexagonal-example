package com.example.port.outbound

import com.example.entity.StatCache

interface StatCacheQueryPort {
    suspend fun query(): StatCache
}