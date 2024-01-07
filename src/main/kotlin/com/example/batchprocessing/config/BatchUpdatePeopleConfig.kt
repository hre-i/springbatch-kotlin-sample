package com.example.batchprocessing.config

import com.example.batchprocessing.listener.JobCompletionNotificationListener
import com.example.batchprocessing.model.People
import com.example.batchprocessing.repository.PeopleRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.transaction.PlatformTransactionManager

/**
 * JPA を使って、一つのテーブルの値を読み/更新/書き込みを行うバッチのサンプル
 *
 * - reader/processor/writer は @Bean をつけずに実装してみる
 */
@Configuration
class BatchUpdatePeopleConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val peopleRepository: PeopleRepository,
) {
    /**
     * RepositoryItemReader のサンプル
     */
    private fun peopleReader(): ItemReader<People> =
        RepositoryItemReaderBuilder<People>()
            .repository(peopleRepository)
            .name("peopleReader") // 必ず必要。別のリーダと重複しないように
            .methodName("findAll")
            // .arguments(listOf())
            .sorts(mapOf("personId" to Sort.Direction.ASC))
            .pageSize(100)
            .build()

    /**
     * ItemProcessor のサンプル
     * - People を受け取って People? を返す
     * - null を返した場合は、ItemWriter に渡されない
     */
    private fun peopleProcessor(): ItemProcessor<People, People?> =
        object : ItemProcessor<People, People?> {
            override fun process(item: People): People? {
                if (item.firstName == "JOHN") {
                    return null
                } else {
                    item.lastName = "Down"
                    return item
                }
            }
        }

    /**
     * ItemWriter のサンプル
     */
    private fun peopleWriter(): ItemWriter<People> =
        JpaItemWriterBuilder<People>()
            .entityManagerFactory(entityManagerFactory)
            .build()

    /**
     * Step の定義
     *
     * - ここでは、reader/processor/writer をインジェクションを使わずに、
     *   上で定義した関数を使って作成している。
     * - peopleReader の pageSize() で指定した値と、
     *   chunk() で指定する chunkSize との関連は未確認。
     */
    @Bean
    fun updatePeopleStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step =
        StepBuilder("updatePeopleStep", jobRepository)
            .chunk<People, People>(3, transactionManager)
            .reader(peopleReader())
            .processor(peopleProcessor())
            .writer(peopleWriter())
            .build()

    /**
     * Job の定義
     */
    @Bean
    fun updatePeopleJob(
        jobRepository: JobRepository,
        updatePeopleStep: Step,
        listener: JobCompletionNotificationListener,
    ): Job =
        JobBuilder("updatePeople", jobRepository)
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .start(updatePeopleStep)
            .build()
}
