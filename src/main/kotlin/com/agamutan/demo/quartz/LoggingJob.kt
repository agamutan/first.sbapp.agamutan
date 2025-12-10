package com.agamutan.demo.quartz

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LoggingJob : Job {
    private val log = LoggerFactory.getLogger(LoggingJob::class.java)

    override fun execute(context: JobExecutionContext) {
        val jobKey = context.jobDetail.key
        val dataMap = context.jobDetail.jobDataMap
        log.info("Executing LoggingJob: jobKey={} jobData={}", jobKey, dataMap)
    }
}