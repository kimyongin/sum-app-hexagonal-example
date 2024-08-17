package com.example.port.outbound

interface InterimResultLoadPort {
    fun load(id: String): Long?
}