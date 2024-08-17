package com.example.port.outbound

interface EventHistoryQueryPort {
    fun query(id: String): List<Long>
}