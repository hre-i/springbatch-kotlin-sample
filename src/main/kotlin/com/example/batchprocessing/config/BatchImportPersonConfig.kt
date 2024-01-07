package com.example.batchprocessing.config

import com.example.batchprocessing.listener.JobCompletionNotificationListener
import com.example.batchprocessing.model.Person
import com.example.batchprocessing.processor.PersonItemProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * CSV ファイルから読み込んだデータを、JDBC を使って DB に追加するサンプル。
 */
@Configuration
class BatchImportPersonConfig {
    @Bean
    fun personReader(): ItemReader<Person> =
        FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(ClassPathResource("sample-data.csv"))
            .delimited()
            .names("firstName", "lastName")
            .targetType(Person::class.java)
            .build()

    @Bean
    fun personProcessor(): ItemProcessor<Person, Person> = PersonItemProcessor()

    @Bean
    fun personWriter(dataSource: javax.sql.DataSource): ItemWriter<Person> =
        JdbcBatchItemWriterBuilder<Person>()
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource!!)
            .beanMapped()
            .build()

    @Bean
    fun importPersonStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        personReader: ItemReader<Person>,
        personProcessor: ItemProcessor<Person, Person?>,
        personWriter: ItemWriter<Person>,
    ): Step =
        StepBuilder("importPersonStep", jobRepository)
            .chunk<Person, Person>(3, transactionManager)
            .reader(personReader)
            .processor(personProcessor)
            .writer(personWriter)
            .build()

    @Bean
    fun importPersonJob(
        jobRepository: JobRepository,
        importPersonStep: Step,
        listener: JobCompletionNotificationListener,
    ): Job =
        JobBuilder("importPerson", jobRepository)
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .start(importPersonStep)
            .build()
}
