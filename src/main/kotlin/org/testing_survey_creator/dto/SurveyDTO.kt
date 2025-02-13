package org.testing_survey_creator.dto

// A DTO (Data Transfer Object) is a design pattern used to transfer data between different layers of an application,
// without exposing the database entities (Survey, Instruction) directly to the API

data class SurveyDTO(
    val id: Long? = null,
    val issueNumber: Int,
    val issueLink: String,
    val taskNumber: Int,
    val taskTitle: String,
    val instructions: List<String>
)
