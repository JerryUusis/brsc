package org.testing_survey_creator

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.*
import org.testing_survey_creator.model.SurveyDTO
import org.testing_survey_creator.service.SurveyService
import org.testing_survey_creator.util.AbstractIntegrationTest
import java.net.URI

// https://spring.io/guides/tutorials/spring-boot-kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SurveyControllerIntegrationTests @Autowired constructor(
    val restTemplate: TestRestTemplate,
    val surveyService: SurveyService
) : AbstractIntegrationTest() {
    @Test
    fun `Should return all surveys`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/surveys")

        // Assert response status
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotEmpty()

        // Parse JSON response body into a queryable JSON document so it can be queried
        val documentContext: DocumentContext = JsonPath.parse(response.body)

        // Convert LinkedHashMap list into List<SurveyDTO>
        // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/map.html
        val actualSurveys: List<SurveyDTO> =
            documentContext.read<List<Map<String, Any>>>("$") // Each JSON object is stored as a LinkedHashMap<String, Any> in a List<Map<String, Any>>
                .map { map ->
                    SurveyDTO(
                        issueNumber = (map["issueNumber"] as Number).toInt(), // map example = "issueNumber" to 124
                        issueLink = map["issueLink"] as String, // map example "issueLink" to "www.example.com"
                        taskNumber = (map["taskNumber"] as Number).toInt(),
                        taskTitle = map["taskTitle"] as String,
                        instructions = (map["instructions"] as List<*>).filterIsInstance<String>() // Removes non-Strings https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/filter-is-instance.html
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
        val expectedSurvey = surveyService.getSingleSurvey(1L)
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/surveys/1")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotEmpty()

        val documentContext: DocumentContext = JsonPath.parse(response.body)

        // Extract actual response data with JSONPath Query expressions
        // https://github.com/json-path/JsonPath?tab=readme-ov-file#operators
        val actualSurvey = SurveyDTO(
            id = documentContext.read<Long?>("$.id").toLong(),
            issueNumber = documentContext.read("$.issueNumber"),
            issueLink = documentContext.read("$.issueLink"),
            taskNumber = documentContext.read("$.taskNumber"),
            taskTitle = documentContext.read("$.taskTitle"),
            instructions = documentContext.read("$.instructions"),
        )
        assertThat(actualSurvey).isEqualTo(expectedSurvey)
    }

    @Test
    fun `Post should return created and include location in location header`() {
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

        // Extract the id from the location header of response object
        val surveyId = actualLocation!!.path.substringAfterLast("/")
        assertThat(surveyId).isNotEmpty()

        val expectedLocation = URI.create("${restTemplate.rootUri}/api/surveys/${surveyId}")
        assertThat(actualLocation).isEqualTo(expectedLocation)

        // Check that newly created resource can be found from the expected location
        val getResponse = restTemplate.getForEntity(expectedLocation, String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(getResponse.body).isNotEmpty()

        val documentContext: DocumentContext = JsonPath.parse(getResponse.body)

        // Create SurveyDTO object from actual data from the response object
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