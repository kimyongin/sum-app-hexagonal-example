package com.example.engine

import com.example.entity.Stat
import com.example.port.inbound.StatQueryPort
import com.example.port.outbound.StatCacheQueryPort
import com.example.port.outbound.StatRuntimeQueryPort

class StatProcessor(
    private val statCacheQueryPort: StatCacheQueryPort,
    private val statRuntimeQueryPort: StatRuntimeQueryPort
) : StatQueryPort {
    override suspend fun query(): Stat {
        val cacheStat = statCacheQueryPort.query()
        val runtimeStat = statRuntimeQueryPort.query()
        return Stat(cacheStat, runtimeStat)
    }
}