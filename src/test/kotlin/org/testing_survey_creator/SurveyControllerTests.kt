package org.testing_survey_creator

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.testing_survey_creator.model.SurveyDTO
import org.testing_survey_creator.repository.SurveyRepository
import org.testing_survey_creator.service.SurveyService
import java.net.URI

// https://spring.io/guides/tutorials/spring-boot-kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SurveyControllerIntegrationTests @Autowired constructor(
    private val restTemplate: TestRestTemplate,
) {

    @Autowired
    private lateinit var surveyService: SurveyService

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

        // Convert LinkedHashMap list into List<SurveyDTO>
        val actualSurveys: List<SurveyDTO> = documentContext.read<List<Map<String, Any>>>("$")
            .map { map ->
                SurveyDTO(
                    issueNumber = (map["issueNumber"] as Number).toInt(),
                    issueLink = map["issueLink"] as String,
                    taskNumber = (map["taskNumber"] as Number).toInt(),
                    taskTitle = map["taskTitle"] as String,
                    instructions = map["instructions"] as List<String>
                )
            }

        val expectedSurveys = surveyService.getAllSurveys()

        assertThat(actualSurveys.size).isEqualTo(expectedSurveys.size)

        // Loop through expected surveys array and assert them equal with actual surveys
        for ((index, expectedSurvey) in expectedSurveys.withIndex()) {
            val actualSurvey: SurveyDTO = actualSurveys[index]
            assertThat(actualSurvey).isEqualTo((expectedSurvey))
        }
    }

    @Test
    fun `Should return single survey with id`() {
        val expectedSurvey = surveyService.getSingleSurvey(1)
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/surveys/1")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotEmpty()

        val documentContext: DocumentContext = JsonPath.parse(response.body)

        // Extract actual response data
        val actualSurvey = SurveyDTO(
            issueNumber = documentContext.read("$.issueNumber"),
            issueLink = documentContext.read("$.issueLink"),
            taskNumber = documentContext.read("$.taskNumber"),
            taskTitle = documentContext.read("$.taskTitle"),
            instructions = documentContext.read("$.instructions"),
        )

        assertThat(actualSurvey).isEqualTo(expectedSurvey)
    }

    @Test
    fun `Post should return created and IncludeLocation`() {
        val newSurvey = SurveyDTO(
            issueNumber = 126,
            issueLink = "https://www.example3.com",
            taskTitle = "Stay focused",
            instructions = listOf("Don't give up", "Dream"),
            taskNumber = 1
        )
        val request = HttpEntity(newSurvey, HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON })

        val response = restTemplate.postForEntity("/api/surveys", request, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val actualLocation = response.headers.location
        assertThat(actualLocation).isNotNull()

        val surveyId = actualLocation!!.path.substringAfterLast("/")
        assertThat(surveyId).isNotEmpty()

        val expectedLocation = URI.create("${restTemplate.rootUri}/api/surveys/${surveyId}")
        assertThat(actualLocation).isEqualTo(expectedLocation)

        val getResponse = restTemplate.getForEntity(expectedLocation, String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(getResponse.body).isNotEmpty()

        val documentContext: DocumentContext = JsonPath.parse(getResponse.body)

        val actualSurvey = SurveyDTO(
            issueNumber = documentContext.read("$.issueNumber"),
            issueLink = documentContext.read("$.issueLink"),
            taskNumber = documentContext.read("$.taskNumber"),
            taskTitle = documentContext.read("$.taskTitle"),
            instructions = documentContext.read("$.instructions"),
        )

        assertThat(newSurvey).isEqualTo(actualSurvey)
    }
}