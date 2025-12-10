package com.agamutan.demo.quartz

import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class QuartzSchedulerService(private val scheduler: Scheduler) {
    private val log = LoggerFactory.getLogger(QuartzSchedulerService::class.java)

    /**
     * Schedule a recurring HTTP job that calls your app endpoints.
     * - scheduleId used as job/trigger name
     * - intervalMinutes: how often the job runs
     * - action: create|update|delete
     * - payload: optional body for create/update
     * - targetId: optional id for update/delete
     */
    fun scheduleTaskAction(
        scheduleId: String,
        intervalMinutes: Int,
        action: String,
        payload: Any? = null,
        targetId: String? = null
    ): Boolean {
        val jobKey = JobKey.jobKey(scheduleId)
        if (scheduler.checkExists(jobKey)) {
            log.warn("Job {} already exists", scheduleId)
            return false
        }

        val jobDetail = JobBuilder.newJob(TaskHttpJob::class.java)
            .withIdentity(jobKey)
            .usingJobData(buildJobDataMap(action, payload, targetId))
            .storeDurably(false)
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withIdentity(TriggerKey.triggerKey(scheduleId))
            .forJob(jobDetail)
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(intervalMinutes)
                    .repeatForever()
            )
            .build()

        scheduler.scheduleJob(jobDetail, trigger)
        log.info("Scheduled task-action {} with interval {} minutes", scheduleId, intervalMinutes)
        return true
    }

    fun updateTaskSchedule(scheduleId: String, newIntervalMinutes: Int, payload: Any? = null, targetId: String? = null): Boolean {
        val jobKey = JobKey.jobKey(scheduleId)
        if (!scheduler.checkExists(jobKey)) {
            log.warn("Job {} does not exist for update", scheduleId)
            return false
        }

        val triggerKey = TriggerKey.triggerKey(scheduleId)
        val newTrigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerKey)
            .forJob(jobKey)
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(newIntervalMinutes)
                    .repeatForever()
            )
            .usingJobData(buildJobDataMap("", payload, targetId)) // if you want to replace data, otherwise keep old
            .build()

        scheduler.rescheduleJob(triggerKey, newTrigger)
        log.info("Rescheduled {} to interval {} minutes", scheduleId, newIntervalMinutes)
        return true
    }

    fun deleteSchedule(scheduleId: String): Boolean {
        val jobKey = JobKey.jobKey(scheduleId)
        if (!scheduler.checkExists(jobKey)) {
            log.warn("Job {} does not exist", scheduleId)
            return false
        }
        val deleted = scheduler.deleteJob(jobKey)
        log.info("Deleted job {} result={}", scheduleId, deleted)
        return deleted
    }

    fun listSchedules(): List<String> {
        val groups = scheduler.jobGroupNames
        val ids = mutableListOf<String>()
        for (group in groups) {
            val keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))
            for (k in keys) {
                ids.add(k.name)
            }
        }
        return ids
    }

    private fun buildJobDataMap(action: String, payload: Any?, targetId: String?): JobDataMap {
        val map = JobDataMap()
        if (action.isNotBlank()) map.put("action", action)
        if (payload != null) map.put("payload", payload)
        if (!targetId.isNullOrBlank()) map.put("targetId", targetId)
        return map
    }
}