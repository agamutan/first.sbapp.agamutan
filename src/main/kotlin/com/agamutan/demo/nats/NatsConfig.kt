package com.agamutan.demo.nats

import io.nats.client.Connection
import io.nats.client.Nats
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PreDestroy

@Configuration
class NatsConfig(private val props: NatsProperties) {
    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var nc: Connection

    @Bean
    fun natsConnection(): Connection {
        nc = Nats.connect(props.url)
        log.info("Connected to NATS at {}", props.url)
        return nc
    }

    @PreDestroy
    fun close() {
        try {
            if (::nc.isInitialized) {
                nc.close()
                log.info("NATS connection closed")
            }
        } catch (ex: Exception) {
            log.warn("Error closing NATS connection", ex)
        }
    }
}