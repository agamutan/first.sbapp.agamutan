package com.agamutan.natslistener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NatsListenerApplication

fun main(args: Array<String>) {
    runApplication<NatsListenerApplication>(*args)
}
