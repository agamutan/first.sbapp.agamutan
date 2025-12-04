package com.agamutan.demo.dto

data class TaskUpdateDto(
    val title: String,
    val description: String,
    val completed: Boolean
)