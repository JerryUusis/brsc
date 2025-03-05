package org.testing_survey_creator.integration.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.testing_survey_creator.dto.UserRegistrationDTO
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.util.AbstractIntegrationTest
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val restTemplate: TestRestTemplate
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `Should create user`() {
        val usersAtStart = userRepository.findAll().size
        val newUser = UserRegistrationDTO("testuser", "testuser@testuser", "testpassword")
        val registrationResponse = restTemplate.postForEntity("/api/users", newUser, UserRegistrationDTO::class.java)
        assertThat(registrationResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val usersAtEnd = userRepository.findAll().size
        assertThat(usersAtEnd).isEqualTo(usersAtStart + 1)
    }

    @Test
    fun `Should throw exception when trying to create a duplicate user`() {
        val newUser = UserRegistrationDTO("testuser", "testuser@testuser", "testpassword")
        val registrationResponse = restTemplate.postForEntity("/api/users", newUser, UserRegistrationDTO::class.java)
        assertThat(registrationResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val entity = HttpEntity(
            UserRegistrationDTO("testuser", "testuser@testuser", "testpassword"),
            HttpHeaders().apply { set("Content-Type", "application/problem+json") },
        )

        val errorResponse =
            restTemplate.exchange(URI("/api/users"), HttpMethod.POST, entity, String::class.java)
        assertThat(errorResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}