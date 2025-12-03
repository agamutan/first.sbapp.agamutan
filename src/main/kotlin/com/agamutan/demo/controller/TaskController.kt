package com.agamutan.demo.controller

import com.agamutan.demo.dto.TaskDto
import com.agamutan.demo.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskDto>> =
        ResponseEntity.ok(taskService.getAllTasks())

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskDto> {
        val task = taskService.getTaskById(id)
        return if (task != null) ResponseEntity.ok(task)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createTask(@RequestBody taskDto: TaskDto): ResponseEntity<TaskDto> {
        val created = taskService.createTask(taskDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: Long, @RequestBody taskDto: TaskDto): ResponseEntity<TaskDto> {
        val updated = taskService.updateTask(id, taskDto)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<Void> {
        return if (taskService.deleteTask(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}