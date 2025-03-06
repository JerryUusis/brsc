package org.testing_survey_creator.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import org.testing_survey_creator.repository.UserRepository

/**
 * Loads user-specific data by the provided username.
 *
 * This method is called by Spring Security during the authentication process. It:
 * 1. Queries the database (via UserRepository) for a user matching the provided email.
 * 2. Throws a UsernameNotFoundException if no such user exists.
 * 3. Maps the retrieved user entity into a UserDetails object that Spring Security uses for authentication.
 *
 * @param email The unique identifier for the user (check LoginService).
 * @return A UserDetails object containing the user's credentials and granted authorities.
 * @throws UsernameNotFoundException if the user is not found.
 */
// https://www.youtube.com/watch?v=c9qCrekFTG4
@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        // 1. Retrieve the user from the database using the provided email.
        // If no user is found, throw UsernameNotFoundException.
        val userEntity = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User with email '$email' not found") }
        // 2. Convert the user entity into a Spring Security UserDetails object.
        // The builder provided by Spring Security's User class is used to construct the object.
        return User
            .withUsername(userEntity.email) // Set the identifier (which is email in current implementation)
            .password(userEntity.passwordHash)
            .authorities(userEntity.roles.map { role -> SimpleGrantedAuthority(role.name) })
            .build()
    }
}