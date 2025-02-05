package org.testing_survey_creator.repository

import org.testing_survey_creator.model.Survey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SurveyRepository : JpaRepository<Survey, Long> {
    /**
     * Finds a survey by issue number.
     * @param issueNumber The issue number to search for.
     * @return The survey if found, or null.
     */

    fun findByIssueNumber(issueNumber: Int): Survey?
}