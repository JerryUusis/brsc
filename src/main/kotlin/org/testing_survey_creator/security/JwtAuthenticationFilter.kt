package org.testing_survey_creator.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    /**
     * This filter intercepts every incoming HTTP request and:
     * 1. Extracts the JWT token from the Authorization header.
     * 2. Validates the token using the JwtUtil.
     * 3. If the token is valid, loads the user details and creates an Authentication object.
     * 4. Sets the Authentication object into the SecurityContext, so the request is processed as authenticated.
     */

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Retrieve the 'Authorization' header from the HTTP request
        val authHeader = request.getHeader("Authorization")

        // Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extract the token by removing the "Bearer " prefix
            val token = authHeader.substring(7)

            // Use JwtUtil to Extract username from the token
            val username = jwtUtil.extractUsernameFromToken(token)

            // Proceed only if we got a username and there's no authentication set in the current context.
            if (username != null && SecurityContextHolder.getContext().authentication == null) {

                // Load user details (which includes password, roles, etc.) from your database or user store.
                val userDetails = userDetailsService.loadUserByUsername(username)

                // Validate the token: check if it's valid and not expired by comparing the token's subject with the user details.
                if (jwtUtil.isTokenValid(token, userDetails.username)) {

                    // Create an authentication object with the user details.
                    // The 'null' credentials mean we're not re-checking the password at this point.
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )

                    // Set additional details on the authentication object, such as the remote IP and session info.
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // Place the authentication object in the SecurityContext, which marks the request as authenticated.
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
