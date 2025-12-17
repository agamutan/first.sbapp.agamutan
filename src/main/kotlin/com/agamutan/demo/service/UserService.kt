package com.agamutan.demo.service

import com.agamutan.demo.model.User
import com.agamutan.demo.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User as SecUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleService: RoleService,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService { // implements UserDetailsService for Spring Security

    // Existing public API kept as-is
    fun listAll(): List<User> = userRepository.findAll()

    fun get(id: Long): User = userRepository.findById(id).orElseThrow { RuntimeException("User not found") }

    @Transactional
    fun create(email: String, username: String, rawPassword: String, roleNames: Set<String>): User {
        if (userRepository.existsByUsername(username)) throw RuntimeException("username exists")
        if (userRepository.existsByEmail(email)) throw RuntimeException("email exists")
        val encoded = passwordEncoder.encode(rawPassword)
        val roles = roleNames.mapNotNull { roleService.findByName(it) }.toMutableSet()
        val user = User(email = email, username = username, password = encoded, roles = roles)
        return userRepository.save(user)
    }

    @Transactional
    fun update(id: Long, email: String?, username: String?, roleNames: Set<String>?): User {
        val user = get(id)
        email?.let { user.email = it }
        username?.let { user.username = it }
        if (roleNames != null) {
            user.roles = roleNames.mapNotNull { roleService.findByName(it) }.toMutableSet()
        }
        return userRepository.save(user)
    }

    @Transactional
    fun delete(id: Long) = userRepository.deleteById(id)

    // Implementation for Spring Security ------------------------------------
    override fun loadUserByUsername(username: String): UserDetails {
        // Unwrap Optional properly (do not use ?: on Optional)
        val user: User = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }

        val authorities = user.roles
            .map { SimpleGrantedAuthority("ROLE_${it.name}") }

        return SecUser.builder()
            .username(user.username)
            .password(user.password) // password must be the encoded/bcrypt hash
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}