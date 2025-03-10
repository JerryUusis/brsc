package org.testing_survey_creator.unit.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testing_survey_creator.dto.SurveyDTO
import org.testing_survey_creator.model.Instruction
import org.testing_survey_creator.model.Survey
import org.testing_survey_creator.repository.SurveyRepository
import org.testing_survey_creator.service.SurveyService

@ExtendWith(MockKExtension::class)
class SurveyServiceTests {

    @MockK
    private lateinit var surveyRepository: SurveyRepository

    private lateinit var surveyService: SurveyService

    @BeforeEach
    fun setup() {
        surveyService = SurveyService(surveyRepository)
    }

    @Test
    fun `getAllSurveys should call findAll from surveyRepository`() {
        every { surveyRepository.findAll() } returns listOf()
        surveyService.getAllSurveys()
        verify(exactly = 1) { surveyRepository.findAll() }
    }
}