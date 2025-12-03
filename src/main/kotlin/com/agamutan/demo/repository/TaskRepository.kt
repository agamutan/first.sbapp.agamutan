package com.agamutan.demo.repository

import com.agamutan.demo.model.Task
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<Task, Long>