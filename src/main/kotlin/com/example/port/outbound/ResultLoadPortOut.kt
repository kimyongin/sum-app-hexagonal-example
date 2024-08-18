package com.example.port.outbound

import com.example.entity.Result

interface ResultLoadPortOut {
    fun load(id: String): Result?
}