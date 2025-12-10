package com.agamutan.demo.web

import com.agamutan.demo.quartz.QuartzSchedulerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks/schedules")
class ScheduleController(private val schedulerService: QuartzSchedulerService) {

    @PostMapping
    fun create(@RequestBody req: ScheduleRequest): ResponseEntity<Any> {
        val created = schedulerService.scheduleTaskAction(req.id, req.intervalMinutes, req.action, req.payload, req.targetId)
        return if (created) ResponseEntity.ok(mapOf("status" to "created", "id" to req.id))
        else ResponseEntity.status(409).body(mapOf("status" to "exists", "id" to req.id))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody req: ScheduleRequest): ResponseEntity<Any> {
        val updated = schedulerService.updateTaskSchedule(id, req.intervalMinutes, req.payload, req.targetId)
        return if (updated) ResponseEntity.ok(mapOf("status" to "updated", "id" to id))
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Any> {
        val deleted = schedulerService.deleteSchedule(id)
        return if (deleted) ResponseEntity.ok(mapOf("status" to "deleted", "id" to id))
        else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun list(): ResponseEntity<Any> {
        val list = schedulerService.listSchedules()
        return ResponseEntity.ok(mapOf("jobs" to list))
    }
}