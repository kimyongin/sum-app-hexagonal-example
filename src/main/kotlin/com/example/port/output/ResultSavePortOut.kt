package com.example.port.output

import com.example.entity.Result

interface ResultSavePortOut {
    fun save(id: String, result: Result)
}