package org.testing_survey_creator.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@Profile("dev")
class DevSecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }  // Enables CORS using the custom CorsFilter
            .csrf { it.disable() }  // Disable CSRF for testing APIs easily
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll() // Allow all requests in dev mode
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        return http.build()
    }

    // https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()

        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
