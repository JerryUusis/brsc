package org.testing_survey_creator.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

// Handles all JWT-related operations

@Component
class JwtUtil(private val secretKey: SecretKey) {
    private val expirationMs: Long = 1000 * 60 * 60 // 1 hour

    // https://github.com/jwtk/jjwt?tab=readme-ov-file#creating-a-jwt
    fun generateToken(username: String, roles: List<String>): String {
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles) // https://github.com/jwtk/jjwt?tab=readme-ov-file#custom-claims
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey)
            .compact()
    }

    // https://github.com/jwtk/jjwt?tab=readme-ov-file#reading-a-jwt
    private fun getClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }
    }

    fun extractUsernameFromToken(token: String): String? {
        return getClaims(token)?.subject
    }


    fun extractRoles(token: String): List<String> {
        val claims = getClaims(token)
        return claims?.get("roles", List::class.java)?.map { it.toString() } ?: emptyList()
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getClaims(token)?.expiration
        return expiration?.before(Date()) ?: true
    }

    fun isTokenValid(token: String, username: String): Boolean {
        val extractedUsername = extractUsernameFromToken(token)
        return extractedUsername == username && !isTokenExpired(token)
    }
}