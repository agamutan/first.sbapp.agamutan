package com.agamutan.demo.dto

data class TaskCreateDto(
    val title: String,
    val description: String,
    val completed: Boolean = false
)