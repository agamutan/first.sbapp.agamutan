package com.agamutan.demo.config

import io.nats.client.Connection
import io.nats.client.Nats
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PreDestroy

@Configuration
class NatsConfig {

    private val logger = LoggerFactory.getLogger(NatsConfig::class.java)

    @Value("\${nats.server-url}")
    private lateinit var natsServerUrl: String

    private var connection: Connection? = null

    @Bean
    fun natsConnection(): Connection {
        return try {
            logger.info("Connecting to NATS server at: {}", natsServerUrl)
            connection = Nats.connect(natsServerUrl)
            logger.info("Successfully connected to NATS server")
            connection!!
        } catch (e: Exception) {
            logger.error("Failed to connect to NATS server: {}", e.message)
            throw e
        }
    }

    @PreDestroy
    fun closeConnection() {
        connection?.let {
            try {
                logger.info("Closing NATS connection")
                it.close()
            } catch (e: Exception) {
                logger.error("Error closing NATS connection: {}", e.message)
            }
        }
    }
}
