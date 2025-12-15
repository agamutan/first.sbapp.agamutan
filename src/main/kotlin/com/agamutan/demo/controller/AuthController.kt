package com.agamutan.demo.controller

import com.agamutan.demo.repository.UserRepository
import com.agamutan.demo.security.JwtUtil
import com.auth0.jwt.JWT
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String, val password: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val user = userRepository.findByUsername(req.username)
            .orElseThrow { RuntimeException("Invalid credentials") }

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw RuntimeException("Invalid credentials")
        }

        val roles = user.roles.map { it.name }
        val token = jwtUtil.generateToken(user.username, roles)

        // Derive cookie maxAge from token expiry so we don't need direct access to jwtUtil.expirationMs
        val expiresAt = JWT.decode(token).expiresAt
        val maxAgeSeconds = if (expiresAt != null) {
            ((expiresAt.time - System.currentTimeMillis()) / 1000).toInt().coerceAtLeast(0)
        } else {
            -1
        }

        val cookie = Cookie("JWT", token)
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.maxAge = maxAgeSeconds
        // cookie.secure = true // enable in production (HTTPS)
        // Optionally set SameSite via response header if required by clients

        response.addCookie(cookie)

        return ResponseEntity.ok(mapOf("username" to user.username, "roles" to roles))
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        val cookie = Cookie("JWT", "")
        cookie.path = "/"
        cookie.maxAge = 0
        cookie.isHttpOnly = true
        // cookie.secure = true
        response.addCookie(cookie)
        return ResponseEntity.ok(mapOf("message" to "Logged out"))
    }
}