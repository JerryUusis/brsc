package org.testing_survey_creator.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import org.testing_survey_creator.dto.LoginDTO
import org.testing_survey_creator.model.Role
import org.testing_survey_creator.security.JwtUtil
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.testing_survey_creator.exception.CustomAuthException
import org.testing_survey_creator.repository.UserRepository

@Service
class LoginService(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    /**
     * Authenticates a user based on the provided login credentials (email and password),
     * and if successful, generates and returns a JWT token.
     *
     * Process Overview:
     * 1. Create an authentication token using the email as the identifier and the provided password.
     * 2. The AuthenticationManager processes this token by delegating to an AuthenticationProvider (e.g., DaoAuthenticationProvider).
     *    - The provider calls CustomUserDetailsService.loadUserByUsername(email) to load user data from the database.
     *    - It then uses the configured PasswordEncoder to compare the provided password with the stored hash.
     * 3. If authentication is successful, an authenticated Authentication object is returned.
     * 4. Extract the granted authorities (roles) and principal (user details) from the Authentication object.
     * 5. Generate a JWT token using the user's unique identifier (email) and roles.
     * 6. Return the generated token.
     */
) {
    fun loginUser(loginDto: LoginDTO): String {
        // 1. Create an authentication token. Here, we're using the email as the username.
        //    This token holds the credentials (email and password) provided by the user.
        val authToken = UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password)

        // Authenticate the token.
        //  - The authenticationManager uses its configured provider (e.g., DaoAuthenticationProvider)
        //    which internally calls CustomUserDetailsService.loadUserByUsername(loginDto.email)
        //    to retrieve the user details from the database.
        //  - The provider then uses the PasswordEncoder to verify that the provided password
        //    matches the stored password hash.
        //  - If successful, it returns an Authentication object that is marked as authenticated.
        try {
            val authentication = authenticationManager.authenticate(authToken)

            // Extract the roles from the Authentication object and map them with Role model
            val roles = authentication.authorities.map { Role(name = it.authority) }.toMutableSet()

            // Extract the authenticated user's details.
            // The principal is cast to Spring Security's User which contains the username, password, and authorities
            val userDetails = authentication.principal as User

            val userEntity = userRepository.findByEmail(loginDto.email)
                .orElseThrow { UsernameNotFoundException("User with email '$loginDto.email not found") }

            // 5. Generate a JWT token.
            //    The JwtUtil.generateToken method creates a token that includes the username, email, id and roles as claims,
            //    sets the issuance and expiration times, and signs it with a secret key.
            return jwtUtil.generateToken(
                userDetails.username, userEntity.email, userEntity.id, roles
            )
        } catch (exception: UsernameNotFoundException) {
            throw CustomAuthException("Invalid credentials", exception)
        } catch (exception: BadCredentialsException) {
            throw CustomAuthException("Authentication failed", exception)
        } catch (exception: AuthenticationException) {
            throw CustomAuthException(exception.message ?: "Authentication error", exception)
        }
    }
}