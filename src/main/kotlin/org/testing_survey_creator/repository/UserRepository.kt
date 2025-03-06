package org.testing_survey_creator.repository

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.testing_survey_creator.model.User
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}