package com.example.batchprocessing.listener

import com.example.batchprocessing.model.Person
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class JobCompletionNotificationListener(private val jdbcTemplate: JdbcTemplate) : JobExecutionListener {
    /**
     *
     */
    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results")

            jdbcTemplate
                .query("SELECT first_name, last_name FROM people", DataClassRowMapper(Person::class.java))
                .forEach(
                    Consumer { person: Person? ->
                        log.info(
                            "Found <{{}}> in the database.",
                            person,
                        )
                    },
                )
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(JobCompletionNotificationListener::class.java)
    }

}
