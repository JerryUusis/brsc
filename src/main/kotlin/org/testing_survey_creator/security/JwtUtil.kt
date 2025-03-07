package org.testing_survey_creator.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import org.testing_survey_creator.model.Role
import java.util.*
import javax.crypto.SecretKey

// Handles all JWT-related operations

@Component
class JwtUtil(private val secretKey: SecretKey) {
    private val expirationMs: Long = 1000 * 60 * 60 // 1 hour

    // https://github.com/jwtk/jjwt?tab=readme-ov-file#creating-a-jwt
    fun generateToken(username: String, email: String, id: Long?, roles: MutableSet<Role>): String {
        return Jwts.builder()
            .subject(username)
            .claim(
                "roles",
                roles.map { it.name }) // Store as a List<String>. More about custom claims: https://github.com/jwtk/jjwt?tab=readme-ov-file#custom-claims
            .claim("email", email)
            .claim("id", id.toString())
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

    fun extractEmailFromToken(token: String): String? {
        return getClaims(token)?.get("email")?.toString()
    }

    fun extractRoles(token: String): MutableSet<Role> {
        val claims = getClaims(token)
        val rolesAsList = claims?.get("roles") as List<*> // Extract the roles claim as a List

        return rolesAsList.mapNotNull { // Filter out null values
            it as? String // Ensure each element is a String
        }.map { Role(name = it) }.toMutableSet() // Convert rolesAsList type from List<String> to MutableSet<Role>
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