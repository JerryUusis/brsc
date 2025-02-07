package org.testing_survey_creator.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("test") // ✅ Only applies in test mode
class TestSecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }  // Disable CSRF (for integration testing)
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll() // Allow all requests in test mode
            }
        return http.build()
    }
}
