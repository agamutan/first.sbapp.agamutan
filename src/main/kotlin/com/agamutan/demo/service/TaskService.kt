package com.agamutan.demo.service

import com.agamutan.demo.dto.TaskDto
import com.agamutan.demo.mapper.TaskMapper
import com.agamutan.demo.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskMapper: TaskMapper
) {
    fun getAllTasks(): List<TaskDto> =
        taskRepository.findAll().map { taskMapper.toDto(it) }

    fun getTaskById(id: Long): TaskDto? =
        taskRepository.findById(id).map { taskMapper.toDto(it) }.orElse(null)

    fun createTask(taskDto: TaskDto): TaskDto {
        val entity = taskMapper.toEntity(taskDto.copy(id = 0)) // id=0 for auto-generation
        val saved = taskRepository.save(entity)
        return taskMapper.toDto(saved)
    }

    fun updateTask(id: Long, taskDto: TaskDto): TaskDto? {
        return if (taskRepository.existsById(id)) {
            val entity = taskMapper.toEntity(taskDto.copy(id = id))
            val saved = taskRepository.save(entity)
            taskMapper.toDto(saved)
        } else null
    }

    fun deleteTask(id: Long): Boolean {
        return if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id)
            true
        } else false
    }
}