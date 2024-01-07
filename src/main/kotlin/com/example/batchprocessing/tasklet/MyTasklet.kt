package com.example.batchprocessing.tasklet

import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Tasklet サンプル
 *
 * - 実行時に次のように引数を渡すことができる
 * - arg1 は必須
 * - arg2 は任意(nullable)
 *
 * ```java
 * java -jar ???.jar arg1=引数 arg2=引数
 * ```
 */
@Component
@StepScope
class MyTasklet(
    @Value("#{jobParameters['arg1']}")
    private val arg1: String,
    @Value("#{jobParameters['arg2']}")
    private val arg2: String?,
) : Tasklet {
    companion object {
        val log = LoggerFactory.getLogger(MyTasklet::class.java)
    }

    override fun execute(
        contribution: StepContribution,
        chunkContext: ChunkContext,
    ): RepeatStatus? {
        log.info("Run tasklet: $this")
        log.info("  arg1=$arg1")
        log.info("  arg2=$arg2")
        return RepeatStatus.FINISHED
    }
}
