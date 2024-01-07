package com.example.batchprocessing.config

import com.example.batchprocessing.listener.JobCompletionNotificationListener
import com.example.batchprocessing.tasklet.MyTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchMyTaskletConfig {
    @Bean
    fun myTaskletJob(
        jobRepository: JobRepository,
        myTaskletStep: Step,
        listener: JobCompletionNotificationListener,
    ): Job =
        JobBuilder("myTasklet", jobRepository)
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .start(myTaskletStep)
            .build()

    @Bean
    fun myTaskletStep(
        jobRepository: JobRepository,
        myTasklet: MyTasklet,
        transactionManager: PlatformTransactionManager,
    ): Step =
        StepBuilder("myTaskletStep", jobRepository)
            .tasklet(myTasklet, transactionManager)
            .build()
}
