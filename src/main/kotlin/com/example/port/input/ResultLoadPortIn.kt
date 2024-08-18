package com.example.port.input

import com.example.entity.Result

interface ResultLoadPortIn {
    fun load(id: String): Result?
}