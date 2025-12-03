package com.agamutan.demo.dto

data class TaskDto(
    val id: Long? = null,
    val title: String,
    val description: String,
    val completed: Boolean = false
)