package com.example.entity

import kotlinx.serialization.Serializable

@Serializable
data class Event(val id: String, val value: Long)