package org.testing_survey_creator.security

import io.jsonwebtoken.Jwts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.crypto.SecretKey

// Provides the SecretKey bean used by JwtUtil for signing and verifying JWT tokens
// Ensures consistent key usage across the application by injecting this SecretKey wherever required.

@Configuration
class SecurityConfig {

    @Bean
    fun secretKey(): SecretKey {
        return Jwts.SIG.HS256.key().build() // Generates a secure random key
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}