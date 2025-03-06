package org.testing_survey_creator.integration.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.testing_survey_creator.dto.LoginDTO
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.util.AbstractIntegrationTest
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTests @Autowired constructor(
    private val restTemplate: TestRestTemplate,
    private val userRepository: UserRepository
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `Should respond with 401 exception with with nonexistent user`() {
        val nonExistingUser = LoginDTO("nonexisting@test.com", "1234password")
        val entity =
            HttpEntity(nonExistingUser, HttpHeaders().apply { set("Content-Type", "application/problem+json") })
        val errorResponse =
            restTemplate.exchange(URI.create("/api/login"), HttpMethod.POST, entity, String::class.java)

        assertThat(errorResponse.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(errorResponse.body).contains("Authentication failed")
    }
}
