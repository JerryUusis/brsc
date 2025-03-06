package org.testing_survey_creator.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object PostgresTestContainer {
    val instance: PostgreSQLContainer<Nothing> =
        PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:latest")).apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_password")
            withInitScript("schema.sql")
            start()
        }
}

// Integration Testing with Testcontainers https://www.youtube.com/watch?v=gF-YG6YZxZk
// https://testcontainers.com/guides/testcontainers-container-lifecycle/#_using_singleton_containers
// Reusable extension to initiate a fresh PostgresSQL database
abstract class AbstractIntegrationTest {
    companion object {
        val postgres: PostgreSQLContainer<Nothing> = PostgresTestContainer.instance

        @BeforeAll
        @JvmStatic
        fun setup() {
            System.setProperty("spring.datasource.url", postgres.jdbcUrl)
            System.setProperty("spring.datasource.username", postgres.username)
            System.setProperty("spring.datasource.password", postgres.password)
        }
    }

    @Test
    fun `Should have container up and running`() {
        Assertions.assertThat(postgres.isRunning).isTrue
    }
}


