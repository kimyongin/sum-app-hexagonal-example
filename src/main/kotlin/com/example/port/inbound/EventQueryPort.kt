package com.example.port.inbound

interface EventQueryPort {
    fun query(id: String): List<Long>
}