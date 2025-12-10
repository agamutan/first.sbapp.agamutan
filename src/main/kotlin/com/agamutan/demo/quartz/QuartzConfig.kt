package com.agamutan.demo.quartz

import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import org.springframework.web.client.RestTemplate

/**
 * Enables Spring autowiring for Quartz Job instances and provides a RestTemplate bean
 * so jobs (like TaskHttpJob) can have RestTemplate injected.
 *
 * Make sure the file path matches the package:
 * src/main/kotlin/com/agamutan/demo/quartz/QuartzConfig.kt
 */
@Configuration
class QuartzConfig : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val jobFactory = object : SpringBeanJobFactory() {
            override fun createJobInstance(bundle: TriggerFiredBundle): Any {
                val job = super.createJobInstance(bundle)
                val beanFactory: AutowireCapableBeanFactory = applicationContext.autowireCapableBeanFactory
                beanFactory.autowireBean(job)
                return job
            }
        }
        return jobFactory
    }

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}