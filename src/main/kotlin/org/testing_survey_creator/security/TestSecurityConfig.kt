package org.testing_survey_creator.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@Profile("test")
class TestSecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter // Automatically injected by Spring
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }  // Disable CSRF (for integration testing)
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/surveys/**").hasRole("ADMIN") // Restrict /surveys/** to ADMIN users
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(
                jwtAuthenticationFilter, // Intercept incoming HTTP and check token in Authorization header
                UsernamePasswordAuthenticationFilter::class.java
            ) // Add JWT filter
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
