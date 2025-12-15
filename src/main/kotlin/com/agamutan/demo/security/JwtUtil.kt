package com.agamutan.demo.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration-ms}") private val expirationMs: Long
) {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun generateToken(username: String, roles: Collection<String>): String {
        val now = Date()
        val expiresAt = Date(now.time + expirationMs)
        val builder = JWT.create()
            .withSubject(username)
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
        if (roles.isNotEmpty()) {
            builder.withArrayClaim("roles", roles.toTypedArray())
        }
        return builder.sign(algorithm)
    }

    fun validateAndGetUsername(token: String): String? {
        val verifier = JWT.require(algorithm).build()
        val decoded = verifier.verify(token)
        return decoded.subject
    }

    fun getRoles(token: String): List<String> {
        val verifier = JWT.require(algorithm).build()
        val decoded = verifier.verify(token)
        val claim = decoded.getClaim("roles")
        return claim.asList(String::class.java) ?: emptyList()
    }
}