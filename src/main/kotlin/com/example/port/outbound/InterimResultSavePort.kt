package com.example.port.outbound

interface InterimResultSavePort {
    fun save(id: String, result: Long)
}