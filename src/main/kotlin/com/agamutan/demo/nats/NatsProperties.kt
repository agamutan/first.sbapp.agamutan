package com.agamutan.demo.nats

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nats")
data class NatsProperties(
    var url: String = "nats://nats:4222",
    var subject: String = "app.events"
)