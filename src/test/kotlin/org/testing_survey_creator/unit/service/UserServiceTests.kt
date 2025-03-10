package org.testing_survey_creator.unit.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import org.testing_survey_creator.dto.UserRegistrationDTO
import org.testing_survey_creator.exception.UserAlreadyExistsException
import org.testing_survey_creator.model.Role
import org.testing_survey_creator.model.User
import org.testing_survey_creator.repository.RoleRepository
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.service.UserService
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class UserServiceTests {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var roleRepository: RoleRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(
            userRepository,
            roleRepository,
            passwordEncoder,
        )
        setupMocksBehaviour()
    }

    private val mockUserRegistrationDto = UserRegistrationDTO(
        "test user",
        "test@test.com",
        "testpassword",
    )

    private val mockUserEntity = User(
        1L,
        mockUserRegistrationDto.username,
        "",
        mockUserRegistrationDto.email,
        Instant.now(),
        mutableSetOf()
    )

    private fun setupMocksBehaviour(userExists: Boolean = false) {
        every { userRepository.findByEmail(eq(mockUserRegistrationDto.email)) } returns
                if (userExists) Optional.of(mockUserEntity)
                else Optional.empty()
        every { roleRepository.findByName(any()) } returns Optional.of(Role(1, "USER"))
        every { passwordEncoder.encode(any()) } returns "passwordHash"
        every { userRepository.save(any()) } returns mockUserEntity
    }

    @Test
    fun `Should throw exception if user already exists with email`() {
        setupMocksBehaviour(userExists = true)
        assertThrows<UserAlreadyExistsException> {
            userService.createUser(mockUserRegistrationDto)
        }
        verify(exactly = 1) { userRepository.findByEmail(eq(mockUserRegistrationDto.email)) }
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { roleRepository.findByName("USER") }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
    }


    @Test
    fun `createUser should call save from userRepository if user doesn't exist`() {
        userService.createUser(mockUserRegistrationDto)
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `createUser should call passwordEncoder if user doesn't exist`() {
        userService.createUser(mockUserRegistrationDto)
        verify(exactly = 1) { passwordEncoder.encode(eq(mockUserRegistrationDto.password)) }
    }

    @Test
    fun `createUser should call findByEmail from userRepository if user doesn't exist`() {
        userService.createUser(mockUserRegistrationDto)
        verify(exactly = 1) { userRepository.findByEmail(eq(mockUserRegistrationDto.email)) }
    }

    @Test
    fun `createUser should call findByName from roleRepository if user doesn't exist`() {
        userService.createUser(mockUserRegistrationDto)
        verify(exactly = 1) { roleRepository.findByName(eq("USER")) }
    }
}