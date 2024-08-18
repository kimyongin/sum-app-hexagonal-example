package com.example.port.input

import com.example.entity.Result

interface ResultSavePortIn {
    fun save(id: String, result: Result)
}