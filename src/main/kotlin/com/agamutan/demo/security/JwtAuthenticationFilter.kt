package com.agamutan.demo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1) Get token from Authorization header (Bearer ...) or from cookie named "JWT"
        val authHeader = request.getHeader("Authorization")
        val tokenFromHeader = if (authHeader?.startsWith("Bearer ") == true) authHeader.substring(7) else null
        val tokenFromCookie = request.cookies?.firstOrNull { it.name == "JWT" }?.value
        val token = tokenFromHeader ?: tokenFromCookie

        if (!token.isNullOrBlank() && SecurityContextHolder.getContext().authentication == null) {
            // 2) Use JwtUtil.validateAndGetUsername to validate token and extract username
            val username = try {
                jwtUtil.validateAndGetUsername(token)
            } catch (ex: Exception) {
                null
            }

            if (!username.isNullOrBlank()) {
                // 3) Load user details and set authentication
                val userDetails = userDetailsService.loadUserByUsername(username)
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}