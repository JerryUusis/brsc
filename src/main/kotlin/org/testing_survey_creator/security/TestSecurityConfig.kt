package org.testing_survey_creator.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("test") // Ensure this only applies to the test profile
class TestSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll() // Allow all requests during tests
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        return http.build()
    }
}
