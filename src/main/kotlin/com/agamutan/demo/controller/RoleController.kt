package com.agamutan.demo.controller

import com.agamutan.demo.dto.RoleResponse
import com.agamutan.demo.dto.CreateRoleRequest
import com.agamutan.demo.service.RoleService
import com.agamutan.demo.model.Role
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/roles")
class RoleController(private val roleService: RoleService) {

    @GetMapping
    fun list(): ResponseEntity<List<RoleResponse>> {
        val roles = roleService.listAll().map { it.toResponse() }
        return ResponseEntity.ok(roles)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<RoleResponse> {
        val role = roleService.get(id)
        return ResponseEntity.ok(role.toResponse())
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody req: CreateRoleRequest): ResponseEntity<RoleResponse> {
        val created: Role = roleService.create(req.name)
        val location = URI.create("/api/roles/${created.id}")
        return ResponseEntity.created(location).body(created.toResponse())
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable id: Long, @RequestBody req: CreateRoleRequest): ResponseEntity<RoleResponse> {
        val updated = roleService.update(id, req.name)
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        roleService.delete(id)
        return ResponseEntity.ok(mapOf("message" to "Role deleted"))
    }

    // Extension/utility to convert Role to RoleResponse
    private fun Role.toResponse() = RoleResponse(this.id, this.name)
}