package com.example.batchprocessing.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * ヘルプメッセージを表示する Job の設定
 */
@Configuration
class BatchHelpMessageConfig {
    @Bean
    fun helpMessageJob(
        jobRepository: JobRepository,
        helpMessageStep: Step,
    ): Job =
        JobBuilder("helpMessage", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(helpMessageStep)
            .build()

    @Bean
    fun helpMessageStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step =
        StepBuilder("helpMessageStep", jobRepository)
            .tasklet(
                object : Tasklet {
                    override fun execute(
                        contribution: StepContribution,
                        chunkContext: ChunkContext,
                    ): RepeatStatus {
                        System.err.println(
                            """
                            usage:
                                java -jar springbatch-sample.jar --job.name=ジョブ名 引数

                                ジョブ名:
                                    help
                                        このメッセージを表示
                                    importPerson
                                        ファイルから読み込んだデータをデータベースに登録
                                    myTasklet arg1=必須引数 arg2=オプション引数
                                        引数サンプル
                            """.trimIndent(),
                        )
                        return RepeatStatus.FINISHED
                    }
                },
                transactionManager,
            )
            .build()
}
