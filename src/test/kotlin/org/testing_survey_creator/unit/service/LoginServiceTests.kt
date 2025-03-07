package org.testing_survey_creator.unit.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.AuthenticationManager
import org.testing_survey_creator.dto.LoginDTO
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.security.JwtUtil
import org.testing_survey_creator.service.LoginService
import org.assertj.core.api.Assertions.assertThat
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.testing_survey_creator.model.Role
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class LoginServiceTests {

    @MockK
    lateinit var authenticationManager: AuthenticationManager

    @MockK
    lateinit var jwtUtil: JwtUtil

    @MockK
    lateinit var userRepository: UserRepository

    private val credentials = LoginDTO("test@user.com", "password")

    private lateinit var loginService: LoginService

    @BeforeEach
    fun setup() {
        loginService = LoginService(authenticationManager, jwtUtil, userRepository)
    }

    @Test
    fun `should call authenticationManager, JwtUitl and userRepository during login with correct parameters`() {
        // Mock Spring Security User object returned as principal from authentication
        val mockSpringUser = User(
            "test@user",
            "testpassword",
            emptyList(),
        )

        // Mock CustomUser model returned from userRepository.findByEmail
        val mockCustomUser = org.testing_survey_creator.model.User(
            1, "test@user.com", "testpassword", "test@user.com",
            Instant.now(), mutableSetOf(Role(1, "USER"))
        )

        // Mock authentication result containing the mockSpringUser as principal
        // Principal is authenticated UserDetails object basically
        val mockAuthentication = UsernamePasswordAuthenticationToken(
            mockSpringUser, null, mockSpringUser.authorities
        )

        // Define the behavior of the mocked dependencies
        every { authenticationManager.authenticate(any()) } returns mockAuthentication
        every { userRepository.findByEmail(any()) } returns Optional.of(mockCustomUser)
        every { jwtUtil.generateToken(any(), any(), any(), any()) } returns "mockJwt"

        val token = loginService.loginUser(credentials)

        // Resolve the roles from the authentication's authorities to match how LoginService extracts them
        val roles = mockAuthentication.authorities.map { Role(name = it.authority) }.toMutableSet()

        // Assert amount of calls each mocked item has been right times and values
        verify(exactly = 1) {
            authenticationManager.authenticate(any())
            jwtUtil.generateToken(
                eq(mockSpringUser.username),
                eq(mockCustomUser.email),
                eq(1),
                eq(roles),
            )
            userRepository.findByEmail(credentials.email)
        }
        assertThat(token).isEqualTo("mockJwt")
    }
}