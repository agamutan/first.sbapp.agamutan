package com.agamutan.demo.service

import io.nats.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class NatsPublisher(
    private val natsConnection: Connection
) {

    private val logger = LoggerFactory.getLogger(NatsPublisher::class.java)

    @Value("\${nats.subject}")
    private lateinit var defaultSubject: String

    fun publish(subject: String = defaultSubject, payload: String) {
        try {
            logger.debug("Publishing message to subject '{}': {}", subject, payload)
            natsConnection.publish(subject, payload.toByteArray(StandardCharsets.UTF_8))
            logger.info("Message published successfully to subject '{}'", subject)
        } catch (e: Exception) {
            logger.error("Failed to publish message to NATS: {}", e.message, e)
        }
    }
}
