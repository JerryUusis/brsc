package org.testing_survey_creator.unit.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testing_survey_creator.dto.SurveyDTO
import org.testing_survey_creator.model.Survey
import org.testing_survey_creator.repository.SurveyRepository
import org.testing_survey_creator.service.SurveyService
import java.util.Optional

@ExtendWith(MockKExtension::class)
class SurveyServiceTests {

    @MockK
    private lateinit var surveyRepository: SurveyRepository

    private lateinit var surveyService: SurveyService

    @BeforeEach
    fun setup() {
        surveyService = SurveyService(surveyRepository)
    }

    private val mockSurveyEntity = Survey(
        1,
        1,
        "www.test.com",
        123,
        "Check for button",
        mutableListOf()
    )

    private val mockSurveyDTO = SurveyDTO(
        1,
        123,
        "www.test.com",
        1,
        "Check for button",
        listOf()
    )

    @Test
    fun `getAllSurveys should call findAll from surveyRepository`() {
        every { surveyRepository.findAll() } returns listOf()
        surveyService.getAllSurveys()
        verify(exactly = 1) { surveyRepository.findAll() }
    }

    @Test
    fun `getSingleSurvey should call findById from surveyRepository`() {
        every { surveyRepository.findById(any()) } returns Optional.of(mockSurveyEntity)
        surveyService.getSingleSurvey(1)
        verify(exactly = 1) { surveyRepository.findById(1) }
    }

    @Test
    fun `createSurvey should call save from surveyRepository`() {
        every { surveyRepository.save(any()) } returns mockSurveyEntity
        surveyService.createSurvey(mockSurveyDTO)
        verify(exactly = 1) { surveyRepository.save(any()) }
    }
}