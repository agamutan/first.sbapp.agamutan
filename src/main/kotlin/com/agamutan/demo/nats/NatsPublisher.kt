package com.agamutan.demo.nats

import io.nats.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class NatsPublisher(
    private val connection: Connection,
    private val props: NatsProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun publish(subject: String = props.subject, payload: String, flushTimeoutMs: Long = 500L) {
        try {
            connection.publish(subject, payload.toByteArray(Charsets.UTF_8))
            connection.flush(Duration.ofMillis(flushTimeoutMs))
        } catch (ex: Exception) {
            log.error("Failed to publish NATS message", ex)
        }
    }
}