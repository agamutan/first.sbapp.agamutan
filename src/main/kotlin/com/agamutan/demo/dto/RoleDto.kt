package com.agamutan.demo.dto

data class CreateRoleRequest(val name: String)
data class RoleResponse(val id: Long?, val name: String)