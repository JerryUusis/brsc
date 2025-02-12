package org.testing_survey_creator.service

import org.springframework.stereotype.Service
import org.testing_survey_creator.model.Instruction
import org.testing_survey_creator.model.Survey
import org.testing_survey_creator.model.SurveyDTO
import org.testing_survey_creator.repository.SurveyRepository

@Service
class SurveyService(private val surveyRepository: SurveyRepository) {

    fun getAllSurveys(): List<SurveyDTO> {
        return surveyRepository.findAll().map { survey ->
            SurveyDTO(
                issueNumber = survey.issueNumber,
                issueLink = survey.issueLink,
                taskNumber = survey.taskNumber,
                taskTitle = survey.taskTitle,
                instructions = survey.instructions.map { it.instructionText }  // Extracting only text
            )
        }
    }

    fun getSingleSurvey(id: Long): SurveyDTO {
        val survey = surveyRepository.findById(id).orElseThrow {
            NoSuchElementException("No survey with ID $id not found")
        }

        return SurveyDTO(
            id = survey.id,
            issueLink = survey.issueLink,
            taskNumber = survey.taskNumber,
            taskTitle = survey.taskTitle,
            instructions = survey.instructions.map { it.instructionText },
            issueNumber = survey.issueNumber
        )
    }

    fun createSurvey(dto: SurveyDTO): Survey {
        val survey = Survey(
            issueNumber = dto.issueNumber,
            issueLink = dto.issueLink,
            taskNumber = dto.taskNumber,
            taskTitle = dto.taskTitle
        )

        val instructions = dto.instructions.map { Instruction(instructionText = it, survey = survey) }
        survey.instructions = instructions.toMutableList() // Ensure mutability

        return surveyRepository.save(survey)
    }

}



