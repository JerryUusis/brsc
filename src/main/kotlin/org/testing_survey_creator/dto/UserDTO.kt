package org.testing_survey_creator.dto

import org.testing_survey_creator.model.Role
import java.time.Instant

data class UserDTO(
    val id: Long? = null,
    val username: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Instant,
    val roles: MutableSet<Role>
)
