package com.example.entity

import kotlinx.serialization.Serializable

@Serializable
data class StatCache(
    val hitCount: Long,
    val missCount: Long,
    val loadSuccessCount: Long,
    val loadFailureCount: Long,
    val totalLoadTime: Long,
    val evictionCount: Long,
    val evictionWeight: Long
)

@Serializable
data class StatRuntime(
    val totalMemory: Long,
    val freeMemory: Long,
    val usedMemory: Long,
    val maxMemory: Long
)

@Serializable
data class Stat(
    val cache: StatCache,
    val runtime: StatRuntime
)