package com.agamutan.demo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(private val jwtFilter: JwtAuthenticationFilter) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager =
        authConfig.authenticationManager

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Disable CSRF for API endpoints to simplify testing
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/api/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            }

            // Enable HTTP Basic authentication for testing
            .httpBasic {}

            // Stateless session management for JWT-based auth
            .sessionManagement { sessions ->
                sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // Authorization rules
            .authorizeHttpRequests { auth ->
                // public endpoints
                auth.requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/v3/api-docs.json",
                    "/api/auth/**" // login/register endpoints
                ).permitAll()

                // admin-only modifications under /api/**
                auth.requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                // everything else under /api/** requires authentication
                auth.requestMatchers("/api/**").authenticated()

                // any other request (e.g. static content) require authentication by default
                auth.anyRequest().authenticated()
            }

        // register JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}