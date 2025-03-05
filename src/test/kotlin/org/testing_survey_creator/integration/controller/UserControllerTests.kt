package org.testing_survey_creator.integration.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.testing_survey_creator.dto.UserRegistrationDTO
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.util.AbstractIntegrationTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val restTemplate: TestRestTemplate
) : AbstractIntegrationTest() {

    @Test
    fun `Should create user`() {
        val usersAtStart = userRepository.findAll().size
        val newUser = UserRegistrationDTO("testuser", "testuser@testuser", "testpassword")
        val registrationResponse = restTemplate.postForEntity("/api/users", newUser, UserRegistrationDTO::class.java)
        assertThat(registrationResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val usersAtEnd = userRepository.findAll().size
        assertThat(usersAtEnd).isEqualTo(usersAtStart + 1)
    }
}