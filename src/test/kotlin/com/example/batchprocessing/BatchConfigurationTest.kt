package com.example.batchprocessing

import com.example.batchprocessing.model.People
import com.example.batchprocessing.repository.PeopleRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener

@ActiveProfiles("test")
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DirtiesContextTestExecutionListener::class,
) // This is to avoid clashing of several JobRepository instances using the same data source for several test classes
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBatchTest
@SpringBootTest
class BatchConfigurationTest {
    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var jobRepositoryTestUtils: JobRepositoryTestUtils

    @Autowired
    private lateinit var peopleRepository: PeopleRepository

    @Autowired
    private lateinit var importPersonJob: Job

    @Autowired
    private lateinit var updatePeopleJob: Job

    @BeforeEach
    fun setUp() {
        peopleRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    @Test
    fun importPersonJob_WhenJobEnds_ThenStatusCompleted() {
        jobLauncherTestUtils.job = importPersonJob
        val jobExecution = jobLauncherTestUtils.launchJob()
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        assertEquals(
            setOf<String>("JILL DOE", "JOE DOE", "JUSTIN DOE", "JANE DOE", "JOHN DOE"),
            peopleRepository.findAll().map { it.firstName + " " + it.lastName }.toSet(),
        )
    }

    @Test
    fun updatePeopleJob_WhenJobEnds_ThenStatusCompleted() {
        peopleRepository.saveAll(
            setOf(
                People(firstName = "JILL", lastName = "DOE"),
                People(firstName = "JOHN", lastName = "DOE"),
            ),
        )
        jobLauncherTestUtils.job = updatePeopleJob
        val jobExecution = jobLauncherTestUtils.launchJob()
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        assertEquals(
            setOf<String>("JILL Down", "JOHN DOE"),
            peopleRepository.findAll().map { it.firstName + " " + it.lastName }.toSet(),
        )
    }
}
