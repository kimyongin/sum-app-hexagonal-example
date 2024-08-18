package com.example.port.outbound

import com.example.entity.Result

interface InterimResultSavePort {
    fun save(id: String, result: Result)
}