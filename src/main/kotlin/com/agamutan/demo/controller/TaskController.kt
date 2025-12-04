package com.agamutan.demo.controller

import com.agamutan.demo.dto.TaskCreateDto
import com.agamutan.demo.dto.TaskUpdateDto
import com.agamutan.demo.model.Task
import com.agamutan.demo.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    private val service: TaskService
) {
    @GetMapping
    fun list(): List<Task> = service.list()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Task = service.get(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: TaskCreateDto): Task = service.create(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TaskUpdateDto): Task =
        service.update(id, dto)

    @PatchMapping("/{id}/completed")
    fun setCompleted(@PathVariable id: Long, @RequestParam completed: Boolean): Task =
        service.setCompleted(id, completed)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = service.delete(id)
}