package org.testing_survey_creator.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.dto.UserRegistrationDTO
import org.testing_survey_creator.exception.UserAlreadyExistsException
import org.testing_survey_creator.model.User
import org.testing_survey_creator.repository.RoleRepository
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(dto: UserRegistrationDTO): User {
        val userRole = roleRepository.findByName("USER").orElseThrow { IllegalStateException("Role not found") }

        val existingUser = userRepository.findByEmail(dto.email).isPresent
        if (existingUser) {
            throw UserAlreadyExistsException("User already exists")
        }

        val newUser = User(
            username = dto.username,
            email = dto.email,
            passwordHash = passwordEncoder.encode(dto.password),
            roles = mutableSetOf(userRole),
            createdAt = Instant.now(),
        )
        return userRepository.save(newUser)
    }
}