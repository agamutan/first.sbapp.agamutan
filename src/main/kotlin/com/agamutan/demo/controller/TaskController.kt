package com.agamutan.demo.controller

import com.agamutan.demo.dto.TaskCreateDto
import com.agamutan.demo.dto.TaskUpdateDto
import com.agamutan.demo.model.Task
import com.agamutan.demo.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    private val service: TaskService
) {
    @Operation(summary = "Acquires the list of all tasks")
    @GetMapping
    fun list(): List<Task> = service.list()

    @Operation(summary = "Displays a task specified by id inputted")
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Task = service.get(id)

    @Operation(summary = "Create a new task along with its description and status, id incremented by one")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: TaskCreateDto): Task = service.create(dto)

    @Operation(summary = "Update an existing task to change its title, description and status, searching by id")
    @PutMapping("/{id}")
    fun update(@Parameter(description = "Value of id to update task details")
        @PathVariable id: Long, @RequestBody dto: TaskUpdateDto): Task =
        service.update(id, dto)

    @Operation(summary = "Finds and updates an existing task's status by id")
    @PatchMapping("/{id}/completed")
    fun setCompleted(@Parameter(description = "Value of id to update status of task")
        @PathVariable id: Long, @RequestParam completed: Boolean): Task =
        service.setCompleted(id, completed)

    @Operation(summary = "Removes an existing task by the corresponding inputted id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@Parameter(description = "Value of id to delete task")
        @PathVariable id: Long) = service.delete(id)
}