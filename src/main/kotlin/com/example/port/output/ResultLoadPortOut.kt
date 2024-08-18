package com.example.port.output

import com.example.entity.Result

interface ResultLoadPortOut {
    fun load(id: String): Result?
}