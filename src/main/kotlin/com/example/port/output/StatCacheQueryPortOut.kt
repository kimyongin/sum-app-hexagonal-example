package com.example.port.output

import com.example.entity.StatCache

interface StatCacheQueryPortOut {
    suspend fun query(): StatCache
}