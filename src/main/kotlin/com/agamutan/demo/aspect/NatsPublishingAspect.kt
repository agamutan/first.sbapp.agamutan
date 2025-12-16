package com.agamutan.demo.aspect

import com.agamutan.demo.service.NatsPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant

@Aspect
@Component
class NatsPublishingAspect(
    private val natsPublisher: NatsPublisher,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(NatsPublishingAspect::class.java)

    @AfterReturning(
        pointcut = "@annotation(postMapping)",
        returning = "result"
    )
    fun afterPostMapping(joinPoint: JoinPoint, postMapping: PostMapping, result: Any?) {
        publishEvent(joinPoint, "POST", postMapping.value.firstOrNull() ?: "", result)
    }

    @AfterReturning(
        pointcut = "@annotation(putMapping)",
        returning = "result"
    )
    fun afterPutMapping(joinPoint: JoinPoint, putMapping: PutMapping, result: Any?) {
        publishEvent(joinPoint, "PUT", putMapping.value.firstOrNull() ?: "", result)
    }

    @AfterReturning(
        pointcut = "@annotation(deleteMapping)",
        returning = "result"
    )
    fun afterDeleteMapping(joinPoint: JoinPoint, deleteMapping: DeleteMapping, result: Any?) {
        publishEvent(joinPoint, "DELETE", deleteMapping.value.firstOrNull() ?: "", result)
    }

    private fun publishEvent(joinPoint: JoinPoint, httpMethod: String, mappingPath: String, result: Any?) {
        try {
            val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
            val path = request?.requestURI ?: mappingPath
            val principal = SecurityContextHolder.getContext().authentication?.name ?: "anonymous"
            
            val body = joinPoint.args.firstOrNull()?.let {
                try {
                    objectMapper.writeValueAsString(it)
                } catch (e: Exception) {
                    null
                }
            }

            val event = mapOf(
                "timestamp" to Instant.now().toString(),
                "subject" to "app.events",
                "httpMethod" to httpMethod,
                "path" to path,
                "principal" to principal,
                "body" to body,
                "status" to "success"
            )

            val payload = objectMapper.writeValueAsString(event)
            natsPublisher.publish(payload = payload)
            
        } catch (e: Exception) {
            logger.error("Error publishing event to NATS: {}", e.message, e)
        }
    }
}
