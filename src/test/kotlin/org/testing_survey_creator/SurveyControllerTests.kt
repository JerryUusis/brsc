package org.testing_survey_creator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import org.testing_survey_creator.model.Survey
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.testing_survey_creator.repository.SurveyRepository

// https://spring.io/guides/tutorials/spring-boot-kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SurveyControllerIntegrationTests @Autowired constructor(
    private val restTemplate: TestRestTemplate,
    private val surveyRepository: SurveyRepository
) {

    @Test
    fun `Should return all surveys`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/surveys")

        // Assert response status
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotEmpty()

        // Parse response from JSON string to JSON document
        val documentContext: DocumentContext = JsonPath.parse(response.body)

        // Query with JSONPath expressions
        // https://github.com/json-path/JsonPath?tab=readme-ov-file#operators
        val length: Int = documentContext.read("$.length()")
        assertThat(length).isEqualTo(2)
    }

    @Test
    fun `Post should return created and IncludeLocation`() {
        val dbLengthAtStart = surveyRepository.count()

        val newSurvey = Survey(
            issueNumber = 103,
            issueLink = "http://example.com/103",
            taskTitle = "Optimize performance",
            instructions = listOf("Step 1: Open console", "Step 2: make it faster")
        )

        val request = HttpEntity(newSurvey, HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON })

        val createResponse = restTemplate.postForEntity("/api/surveys", request, String::class.java)

        val savedSurvey = surveyRepository.findAll().last()

        val expectedLocation = createResponse.headers.location.toString()
        val dbLengthAtEnd = surveyRepository.count()

        // Expected the location header to have the right location
        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(createResponse.headers.location.toString()).endsWith(expectedLocation)
        assertThat(dbLengthAtEnd).isEqualTo(dbLengthAtStart + 1)

        // Fetch the created survey using GET request
        val locationResponse = restTemplate.getForEntity(expectedLocation, String::class.java)
        assertThat(locationResponse.statusCode).isEqualTo(HttpStatus.OK)

        // Deserialize JSON response into a <Survey> Kotlin object
        val objectMapper = jacksonObjectMapper()
        val retrievedSurvey: Survey = objectMapper.readValue(locationResponse.body, Survey::class.java)
        assertThat(retrievedSurvey).isEqualTo(savedSurvey)
    }
}