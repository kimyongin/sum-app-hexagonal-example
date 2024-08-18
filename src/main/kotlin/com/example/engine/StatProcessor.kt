package com.example.engine

import com.example.entity.Stat
import com.example.port.input.StatQueryPortIn
import com.example.port.output.StatCacheQueryPortOut
import com.example.port.output.StatRuntimeQueryPortOut

class StatProcessor(
    private val statCacheQueryPort: StatCacheQueryPortOut,
    private val statRuntimeQueryPortOut: StatRuntimeQueryPortOut
) : StatQueryPortIn {
    override suspend fun query(): Stat {
        val cacheStat = statCacheQueryPort.query()
        val runtimeStat = statRuntimeQueryPortOut.query()
        return Stat(cacheStat, runtimeStat)
    }
}