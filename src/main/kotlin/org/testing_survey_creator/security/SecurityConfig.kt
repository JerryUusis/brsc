package org.testing_survey_creator.security

import io.jsonwebtoken.Jwts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.crypto.SecretKey

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityFilterChain {
        http
            .cors {}
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.POST, "/api/login", "/api/users").permitAll()
                auth.anyRequest().authenticated()
//                auth.anyRequest().permitAll() // Allow all requests
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun jwtUtil(secretKey: SecretKey): JwtUtil {
        return JwtUtil(secretKey)
    }

    @Bean
    fun secretKey(): SecretKey {
        return Jwts.SIG.HS256.key().build() // Generates a secure random key
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/authentication/AuthenticationManager.html
    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html#publish-authentication-manager-bean
    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    // Allow requests from dev environment
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
