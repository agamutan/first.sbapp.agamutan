package com.agamutan.demo.quartz

import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.Scheduler
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Schedules a LoggingJob on application startup so you can verify Quartz is wired correctly.
 * Runs every 10 seconds for quick verification.
 */
@Component
class TestSchedulerRunner(private val scheduler: Scheduler) {
    private val log = LoggerFactory.getLogger(TestSchedulerRunner::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun scheduleTestJob() {
        try {
            val jobKey = org.quartz.JobKey.jobKey("test-logging-job", "test-group")
            if (scheduler.checkExists(jobKey)) {
                log.info("Test job already exists, skipping scheduling")
                return
            }

            val jobDetail = JobBuilder.newJob(LoggingJob::class.java)
                .withIdentity(jobKey)
                .usingJobData(JobDataMap(mapOf("purpose" to "verify-quartz-config")))
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withIdentity(org.quartz.TriggerKey.triggerKey("test-logging-trigger", "test-group"))
                .forJob(jobDetail)
                .startNow()
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever()
                )
                .build()

            scheduler.scheduleJob(jobDetail, trigger)
            log.info("Scheduled test LoggingJob to run every 10 seconds")
        } catch (ex: Exception) {
            log.error("Failed to schedule test job", ex)
        }
    }
}