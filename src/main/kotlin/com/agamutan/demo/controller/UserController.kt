package com.agamutan.demo.controller

import com.agamutan.demo.dto.CreateUserRequest
import com.agamutan.demo.dto.UpdateUserRequest
import com.agamutan.demo.dto.UserResponse
import com.agamutan.demo.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun list(): ResponseEntity<List<UserResponse>> {
        val users = userService.listAll().map {
            UserResponse(it.id, it.email, it.username, it.roles.map { r -> r.name }.toSet())
        }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val u = userService.get(id)
        return ResponseEntity.ok(UserResponse(u.id, u.email, u.username, u.roles.map { it.name }.toSet()))
    }

    @PostMapping
    fun create(@RequestBody req: CreateUserRequest): ResponseEntity<UserResponse> {
        val u = userService.create(req.email, req.username, req.password, req.roleNames)
        return ResponseEntity.ok(UserResponse(u.id, u.email, u.username, u.roles.map { it.name }.toSet()))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: UpdateUserRequest): ResponseEntity<UserResponse> {
        val u = userService.update(id, req.email, req.username, req.roleNames)
        return ResponseEntity.ok(UserResponse(u.id, u.email, u.username, u.roles.map { it.name }.toSet()))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        userService.delete(id)
        return ResponseEntity.ok(mapOf("message" to "deleted"))
    }
}