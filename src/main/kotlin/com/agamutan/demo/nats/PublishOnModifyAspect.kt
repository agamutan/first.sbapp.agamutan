package com.agamutan.demo.nats

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest

@Aspect
@Component
class PublishOnModifyAspect(
    private val publisher: NatsPublisher,
    private val props: NatsProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()

    // Pointcut: any method annotated with PostMapping, PutMapping, or DeleteMapping
    @Around("(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    @Throws(Throwable::class)
    fun aroundModifying(joinPoint: ProceedingJoinPoint): Any? {
        val start = Instant.now()
        val result: Any? = try {
            joinPoint.proceed()
        } catch (t: Throwable) {
            // Optionally publish an error event here
            throw t
        }
        val end = Instant.now()

        try {
            val request = currentHttpRequest()
            val httpMethod = request?.method ?: detectHttpMethod(joinPoint)
            val path = request?.requestURI ?: "unknown"
            val principal = request?.userPrincipal?.name
            val bodyCandidate = extractBody(joinPoint)
            val status = extractStatus(result)

            val payloadMap = mapOf(
                "timestamp" to Instant.now().toString(),
                "subject" to props.subject,
                "httpMethod" to httpMethod,
                "path" to path,
                "principal" to principal,
                "body" to bodyCandidate,
                "status" to status,
                "durationMs" to (end.toEpochMilli() - start.toEpochMilli())
            )

            val payload = mapper.writeValueAsString(payloadMap)
            publisher.publish(props.subject, payload)
        } catch (e: Exception) {
            log.warn("Failed to publish NATS event: ${e.message}", e)
        }

        return result
    }

    private fun currentHttpRequest(): HttpServletRequest? {
        val attrs = RequestContextHolder.getRequestAttributes() ?: return null
        val req = attrs.resolveReference(RequestAttributes.REFERENCE_REQUEST)
        return req as? HttpServletRequest
    }

    private fun detectHttpMethod(joinPoint: ProceedingJoinPoint): String {
        val sig = joinPoint.signature as? MethodSignature ?: return "UNKNOWN"
        val method = sig.method
        return when {
            method.isAnnotationPresent(PostMapping::class.java) -> "POST"
            method.isAnnotationPresent(PutMapping::class.java) -> "PUT"
            method.isAnnotationPresent(DeleteMapping::class.java) -> "DELETE"
            else -> "UNKNOWN"
        }
    }

    private fun extractBody(joinPoint: ProceedingJoinPoint): Any? {
        val args = joinPoint.args ?: return null
        // return the first non-null arg that is not a servlet request/response
        return args.firstOrNull { it != null && it !is HttpServletRequest }
    }

    private fun extractStatus(result: Any?): Int? {
        return when (result) {
            is ResponseEntity<*> -> result.statusCodeValue
            else -> null
        }
    }
}