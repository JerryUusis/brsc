package org.testing_survey_creator.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.testing_survey_creator.model.Survey

@Repository
interface SurveyRepository : JpaRepository<Survey, Long>