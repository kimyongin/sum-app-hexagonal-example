package com.example.port.outbound

interface EventHistorySavePort {
    fun save(id: String, value: Long)
}