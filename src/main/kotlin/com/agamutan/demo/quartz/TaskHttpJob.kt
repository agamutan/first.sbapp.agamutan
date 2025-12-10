package com.agamutan.demo.quartz

import com.fasterxml.jackson.databind.ObjectMapper
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class TaskHttpJob(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : Job {
    private val log = LoggerFactory.getLogger(TaskHttpJob::class.java)

    override fun execute(context: JobExecutionContext) {
        val data = context.jobDetail.jobDataMap
        val action = data.getString("action") ?: run {
            log.warn("TaskHttpJob: no action provided")
            return
        }

        try {
            when (action.lowercase()) {
                "create" -> {
                    val payload = data["payload"]
                    val json = toJson(payload)
                    val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
                    val entity = HttpEntity(json, headers)
                    val url = "http://localhost:8080/api/tasks" // adjust port/path if different
                    val resp = restTemplate.postForEntity(url, entity, String::class.java)
                    log.info("TaskHttpJob create response: status={} body={}", resp.statusCodeValue, resp.body)
                }

                "update" -> {
                    val targetId = data.getString("targetId")
                    if (targetId.isNullOrBlank()) {
                        log.warn("TaskHttpJob update called without targetId")
                        return
                    }
                    val payload = data["payload"]
                    val json = toJson(payload)
                    val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
                    val entity = HttpEntity(json, headers)
                    val url = "http://localhost:8080/api/tasks/$targetId"
                    restTemplate.put(url, entity)
                    log.info("TaskHttpJob update called for id={}", targetId)
                }

                "delete" -> {
                    val targetId = data.getString("targetId")
                    if (targetId.isNullOrBlank()) {
                        log.warn("TaskHttpJob delete called without targetId")
                        return
                    }
                    val url = "http://localhost:8080/api/tasks/$targetId"
                    restTemplate.delete(url)
                    log.info("TaskHttpJob delete called for id={}", targetId)
                }

                else -> {
                    log.warn("TaskHttpJob unknown action={}", action)
                }
            }
        } catch (ex: Exception) {
            log.error("TaskHttpJob failed for action=$action", ex)
        }
    }

    private fun toJson(payload: Any?): String {
        return when (payload) {
            null -> "{}"
            is String -> {
                val s = payload as String
                if (s.trim().startsWith("{") || s.trim().startsWith("[")) s else objectMapper.writeValueAsString(s)
            }
            else -> objectMapper.writeValueAsString(payload)
        }
    }
}