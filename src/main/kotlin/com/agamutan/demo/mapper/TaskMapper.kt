package com.agamutan.demo.mapper

import com.agamutan.demo.dto.TaskCreateDto
import com.agamutan.demo.dto.TaskGetDto
import com.agamutan.demo.dto.TaskUpdateDto
import com.agamutan.demo.model.Task
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

@Mapper(componentModel = "spring")
interface TaskMapper {
    fun toGetDto(entity: Task): TaskGetDto

    @Mapping(target = "id", ignore = true)
    fun fromCreateDto(dto: TaskCreateDto): Task

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateEntityFromDto(dto: TaskUpdateDto, @MappingTarget entity: Task)
}