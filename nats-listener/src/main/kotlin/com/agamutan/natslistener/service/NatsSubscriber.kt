package com.agamutan.natslistener.service

import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Nats
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class NatsSubscriber {

    private val logger = LoggerFactory.getLogger(NatsSubscriber::class.java)

    @Value("\${nats.server-url}")
    private lateinit var natsServerUrl: String

    @Value("\${nats.subject}")
    private lateinit var subject: String

    private var connection: Connection? = null
    private var dispatcher: Dispatcher? = null

    @PostConstruct
    fun init() {
        try {
            logger.info("Connecting to NATS server at: {}", natsServerUrl)
            connection = Nats.connect(natsServerUrl)
            logger.info("Successfully connected to NATS server")

            dispatcher = connection!!.createDispatcher { msg ->
                val payload = String(msg.data, StandardCharsets.UTF_8)
                logger.info("Received message on subject '{}': {}", msg.subject, payload)
                logger.info("Message metadata - Subject: {}, Reply-To: {}, Size: {} bytes", 
                    msg.subject, msg.replyTo ?: "N/A", msg.data.size)
            }

            dispatcher!!.subscribe(subject)
            logger.info("Subscribed to subject: {}", subject)
            
        } catch (e: Exception) {
            logger.error("Failed to initialize NATS subscriber: {}", e.message, e)
            throw e
        }
    }

    @PreDestroy
    fun cleanup() {
        try {
            logger.info("Cleaning up NATS subscriber")
            dispatcher?.unsubscribe(subject)
            connection?.close()
        } catch (e: Exception) {
            logger.error("Error during cleanup: {}", e.message)
        }
    }
}
