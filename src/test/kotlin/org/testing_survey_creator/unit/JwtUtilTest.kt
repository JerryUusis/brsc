package org.testing_survey_creator.unit

import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testing_survey_creator.model.Role
import org.testing_survey_creator.security.JwtUtil
import java.util.*
import javax.crypto.SecretKey

class JwtUtilTest {

    private lateinit var jwtUtil: JwtUtil
    private lateinit var secretKey: SecretKey

    private val roles = mutableSetOf(Role(name = "USER"), Role(name = "ADMIN"))
    private val username = "test username"
    private val id: Long = 1
    private val email = "test@test.com"
    private lateinit var token: String


    @BeforeEach
    fun setUp() {
        // Manually initialize SecretKey for isolated testing
        secretKey = Jwts.SIG.HS256.key().build()

        // Initialize JwtUtil with the generated SecretKey
        jwtUtil = JwtUtil(secretKey)

        token = jwtUtil.generateToken(
            username,
            email,
            id,
            roles
        )
    }

    @Test
    fun `should generate a valid JWT token`() {
        assertNotNull(token) // Ensure token is generated
    }

    @Test
    fun `should extract username from token`() {
        assertEquals(username, jwtUtil.extractUsernameFromToken(token))
    }

    @Test
    fun `should extract roles from token`() {
        assertEquals(roles, jwtUtil.extractRoles(token))
    }

    @Test
    fun `should extract email from token`() {
        assertThat(jwtUtil.extractEmailFromToken(token)).isEqualTo(email)
    }

    @Test
    fun `should validate a correct token`() {
        assertTrue(jwtUtil.isTokenValid(token, username))
    }

    @Test
    fun `should detect an invalid token`() {
        val token = "invalid.token.value"

        assertFalse(jwtUtil.isTokenValid(token, "someUser"))
    }

    @Test
    fun `should detect an expired token`() {
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
        // Generate a valid token
        val validToken = Jwts.builder()
            .subject(username)
            .claim("roles", roles.map { it.name })
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + (1000 * 60 * 60)))
            .signWith(secretKey)
            .compact()

        // Validate the token
        assertTrue(jwtUtil.isTokenValid(validToken, username))
        assertEquals(username, jwtUtil.extractUsernameFromToken(validToken))
        assertEquals(roles, jwtUtil.extractRoles(validToken))
    }
}