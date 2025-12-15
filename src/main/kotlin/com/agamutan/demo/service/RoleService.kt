package com.agamutan.demo.service

import com.agamutan.demo.model.Role
import com.agamutan.demo.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(private val roleRepository: RoleRepository) {
    fun findByName(name: String): Role? = roleRepository.findByName(name).orElse(null)

    fun listAll(): List<Role> = roleRepository.findAll()

    fun get(id: Long): Role = roleRepository.findById(id).orElseThrow { RuntimeException("Role not found") }

    @Transactional
    fun create(name: String): Role = roleRepository.save(Role(name = name))

    @Transactional
    fun update(id: Long, name: String): Role {
        val role = get(id)
        val newRole = role.copy(name = name)
        return roleRepository.save(newRole)
    }

    @Transactional
    fun delete(id: Long) = roleRepository.deleteById(id)
}