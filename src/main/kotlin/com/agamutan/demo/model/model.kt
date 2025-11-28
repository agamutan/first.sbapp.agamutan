package com.agamutan.demo.model

/**
 * Task data class represents a single task in our Task Management system.
 *
 * @property id Unique identifier for the task
 * @property title Short title/name of the task
 * @property description Detailed description of what the task involves
 * @property completed Boolean flag indicating if the task is done or not
 */
data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val completed: Boolean = false
)