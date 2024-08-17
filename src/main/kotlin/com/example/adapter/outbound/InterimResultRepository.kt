package com.example.adapter.outbound

import com.example.entity.StatCache
import com.example.port.outbound.InterimResultLoadPort
import com.example.port.outbound.StatCacheQueryPort
import com.example.port.outbound.InterimResultSavePort
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

class InterimResultRepository : InterimResultLoadPort, InterimResultSavePort, StatCacheQueryPort {
    private val interimResultCache = Caffeine.newBuilder()
        .maximumSize(5)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .recordStats() // 통계 기록 활성화
        .build<String, Long>()

    // 캐시에서 중간 집계값을 불러옴 (없으면 null 반환)
    override fun load(id: String): Long? {
        return interimResultCache.getIfPresent(id)
    }

    // 중간 집계값을 캐시에 저장
    override fun save(id: String, result: Long) {
        interimResultCache.put(id, result)
    }

    // 캐시 통계 조회
    override suspend fun query(): StatCache {
        val stats = interimResultCache.stats()
        return StatCache(
            hitCount = stats.hitCount(),
            missCount = stats.missCount(),
            loadSuccessCount = stats.loadSuccessCount(),
            loadFailureCount = stats.loadFailureCount(),
            totalLoadTime = stats.totalLoadTime(),
            evictionCount = stats.evictionCount(),
            evictionWeight = stats.evictionWeight()
        )
    }
}
