package com.agamutan.demo.config

import com.agamutan.demo.model.Role
import com.agamutan.demo.model.User
import com.agamutan.demo.repository.RoleRepository
import com.agamutan.demo.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory


@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String?) {
        val adminRole = roleRepository.findByName("ADMIN").orElseGet {
            log.info("Creating role ADMIN")
            roleRepository.save(Role(name = "ADMIN"))
        }

        val userRole = roleRepository.findByName("USER").orElseGet {
            log.info("Creating role USER")
            roleRepository.save(Role(name = "USER"))
        }

        if (!userRepository.findByUsername("admin").isPresent) {
            log.info("Creating initial admin user (username=admin, password=admin). Change password immediately.")
            val adminUser = User(
                email = "admin@example.com",
                username = "admin",
                password = passwordEncoder.encode("admin"),
                roles = mutableSetOf(adminRole, userRole)
            )
            userRepository.save(adminUser)
        } else {
            log.info("Admin user already exists, skipping creation.")
        }
    }
}