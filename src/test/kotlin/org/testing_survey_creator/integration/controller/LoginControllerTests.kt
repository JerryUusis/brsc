package org.testing_survey_creator.integration.controller

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
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
import org.testing_survey_creator.dto.UserRegistrationDTO
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.security.JwtUtil
import org.testing_survey_creator.util.AbstractIntegrationTest
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTests @Autowired constructor(
    private val restTemplate: TestRestTemplate,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `Successful login should return valid token`() {
        val newUser = UserRegistrationDTO("testuser", "test@user.com", "testpassword")

        val registrationResponse = restTemplate.postForEntity("/api/users", newUser, UserRegistrationDTO::class.java)
        assertThat(registrationResponse.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(userRepository.findByEmail(newUser.email)).isPresent

        val loginBody = LoginDTO(newUser.email, newUser.password)
        val loginResponse = restTemplate.postForEntity("/api/login", loginBody, String::class.java)
        assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext: DocumentContext = JsonPath.parse(loginResponse.body)
        val token = documentContext.read<String>("$.token")

        assertThat(token).startsWith("eyJhb")
        assertThat(jwtUtil.isTokenValid(
            token,
            newUser.email,
        )).isTrue
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
