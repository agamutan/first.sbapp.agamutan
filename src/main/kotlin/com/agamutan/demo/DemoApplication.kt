package com.agamutan.demo

import com.agamutan.demo.nats.NatsProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(NatsProperties::class)
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}