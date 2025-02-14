package org.testing_survey_creator

import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.testing_survey_creator.model.Role
import org.testing_survey_creator.security.JwtUtil
import org.testing_survey_creator.security.SecurityConfig
import java.util.*
import javax.crypto.SecretKey

@SpringBootTest(classes = [SecurityConfig::class, JwtUtil::class])
// Autowire the constructor for dependency injection
class JwtUtilTest @Autowired constructor(
    private var jwtUtil: JwtUtil,
    private var secretKey: SecretKey
) {
    val roles = mutableSetOf(Role(name = "USER"), Role(name = "ADMIN"))
    @BeforeEach
    fun setUp() {
        // Inject the custom secret key into JwtUtil
        // Initialize JwtUtil with the injected SecretKey
        jwtUtil = JwtUtil(secretKey)
    }

    @Test
    fun `should generate a valid JWT token`() {
        val username = "testUser"

        val token = jwtUtil.generateToken(username, roles)

        assertNotNull(token) // Ensure token is generated
    }

    @Test
    fun `should extract username from token`() {
        val username = "testUser"

        val token = jwtUtil.generateToken(username, roles)

        assertEquals(username, jwtUtil.extractUsernameFromToken(token))
    }

    @Test
    fun `should extract roles from token`() {
        val username = "testUser"

        val token = jwtUtil.generateToken(username, roles)

        assertEquals(roles, jwtUtil.extractRoles(token))
    }

    @Test
    fun `should validate a correct token`() {
        val username = "validUser"
        val token = jwtUtil.generateToken(username, roles)

        assertTrue(jwtUtil.isTokenValid(token, username))
    }

    @Test
    fun `should detect an invalid token`() {
        val token = "invalid.token.value"

        assertFalse(jwtUtil.isTokenValid(token, "someUser"))
    }

    @Test
    fun `should detect an expired token`() {
        val username = "expiredUser"

        // Generate a token with a past expiration date
        val expiredToken = Jwts.builder()
            .subject(username)
            .claim("roles", roles.map { it.name })
            .issuedAt(Date(System.currentTimeMillis() - (1000 * 60 * 60 * 2))) // 2 hours ago
            .expiration(Date(System.currentTimeMillis() - (1000 * 60 * 60))) // 1 hour ago
            .signWith(secretKey)
            .compact()

        // Validate that the token is expired
        assertFalse(jwtUtil.isTokenValid(expiredToken, username))
    }

    @Test
    fun `should validate user with a custom secret key`() {
        val username = "testUser"

        // Generate a valid token
        val validToken = Jwts.builder()
            .subject(username)
            .claim("roles", roles.map { it.name })
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + (1000 * 60 * 60))) // 1 hour from now
            .signWith(secretKey)
            .compact()

        // Validate the token
        assertTrue(jwtUtil.isTokenValid(validToken, username))
        assertEquals(username, jwtUtil.extractUsernameFromToken(validToken))
        assertEquals(roles, jwtUtil.extractRoles(validToken))
    }

}