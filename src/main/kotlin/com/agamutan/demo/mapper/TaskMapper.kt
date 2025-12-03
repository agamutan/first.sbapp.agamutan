package com.agamutan.demo.mapper

import com.agamutan.demo.dto.TaskDto
import com.agamutan.demo.model.Task
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TaskMapper {
    fun toDto(task: Task): TaskDto
    fun toEntity(taskDto: TaskDto): Task
}