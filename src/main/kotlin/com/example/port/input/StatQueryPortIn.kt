package com.example.port.input

import com.example.entity.Stat

interface StatQueryPortIn {
    suspend fun query(): Stat
}