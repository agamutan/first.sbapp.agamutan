package com.agamutan.demo.web

/**
 * id: unique schedule id
 * action: "create" | "update" | "delete"
 * intervalMinutes: recurrence interval in minutes
 * payload: object used as create/update body (example: { "title":"T", "description":"D", "status":"OPEN" })
 * targetId: id for update/delete
 */
data class ScheduleRequest(
    val id: String,
    val action: String,
    val intervalMinutes: Int,
    val payload: Any? = null,
    val targetId: String? = null
)