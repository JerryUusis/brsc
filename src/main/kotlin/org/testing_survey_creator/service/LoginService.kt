package org.testing_survey_creator.service

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.testing_survey_creator.dto.LoginDTO
import org.testing_survey_creator.repository.UserRepository
import org.testing_survey_creator.security.JwtUtil

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
) {
    fun loginUser(loginDto: LoginDTO): String {
        val user = userRepository.findByEmail(loginDto.email)
            ?: throw UsernameNotFoundException("User not found")

        if (!passwordEncoder.matches(loginDto.password, user.passwordHash)) {
            throw BadCredentialsException("Invalid credentials")
        }

        return jwtUtil.generateToken(user.email, user.roles)
    }
}