package org.testing_survey_creator

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class TestingSurveyCreatorApplicationTests {

    @Test
    fun contextLoads() {
    }
}
