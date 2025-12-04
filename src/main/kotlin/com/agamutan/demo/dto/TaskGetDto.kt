package com.agamutan.demo.dto

import java.time.Instant

data class TaskGetDto(
    val id: Long,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)