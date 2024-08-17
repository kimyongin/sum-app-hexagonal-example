package com.example.port.inbound

import com.example.entity.EventResult

interface EventSumPort {
    fun sum(id: String, value: Long): EventResult
}