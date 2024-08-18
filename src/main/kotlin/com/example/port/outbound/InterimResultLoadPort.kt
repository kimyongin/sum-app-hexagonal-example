package com.example.port.outbound

import com.example.entity.Result

interface InterimResultLoadPort {
    fun load(id: String): Result?
}