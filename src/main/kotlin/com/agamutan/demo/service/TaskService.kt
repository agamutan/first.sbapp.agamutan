package com.agamutan.demo.service

import com.agamutan.demo.dto.TaskCreateDto
import com.agamutan.demo.dto.TaskUpdateDto
import com.agamutan.demo.mapper.TaskMapper
import com.agamutan.demo.model.Task
import com.agamutan.demo.repository.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    private val repository: TaskRepository,
    private val mapper: TaskMapper
) {
    fun list(): List<Task> =
        repository.findAll()

    fun get(id: Long): Task =
        repository.findById(id).orElseThrow { NoSuchElementException("Task $id not found") }

    @Transactional
    fun create(dto: TaskCreateDto): Task {
        val entity = mapper.fromCreateDto(dto)
        return repository.save(entity)
    }

    @Transactional
    fun update(id: Long, dto: TaskUpdateDto): Task {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Task $id not found") }
        mapper.updateEntityFromDto(dto, entity)
        return repository.save(entity)
    }

    @Transactional
    fun setCompleted(id: Long, completed: Boolean): Task {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Task $id not found") }
        entity.completed = completed
        return repository.save(entity)
    }

    @Transactional
    fun delete(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("Task $id not found")
        repository.deleteById(id)
    }
}