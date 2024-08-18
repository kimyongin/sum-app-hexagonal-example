package com.example.port.outbound

import com.example.entity.Result

interface ResultSavePortOut {
    fun save(id: String, result: Result)
}