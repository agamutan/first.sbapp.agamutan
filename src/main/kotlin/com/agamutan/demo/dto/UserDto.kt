package com.agamutan.demo.dto

data class CreateUserRequest(
    val email: String,
    val username: String,
    val password: String,
    val roleNames: Set<String> = emptySet()
)

data class UpdateUserRequest(
    val email: String?,
    val username: String?,
    val roleNames: Set<String>?
)

data class UserResponse(
    val id: Long?,
    val email: String,
    val username: String,
    val roles: Set<String>
)