package org.testing_survey_creator

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import org.testing_survey_creator.model.SurveyDTO
import org.testing_survey_creator.service.SurveyService

@RestController
@RequestMapping("/api/surveys")
class SurveyController(private val surveyService: SurveyService) {

    @GetMapping
    fun getAllSurveys(): List<SurveyDTO> {
        return surveyService.getAllSurveys()
    }

    @GetMapping("/{id}")
    fun getSurvey(@PathVariable id: Long): SurveyDTO {
        return surveyService.getSingleSurvey(id)
    }

    @PostMapping
    fun createSurvey(@RequestBody dto: SurveyDTO, ucb: UriComponentsBuilder): ResponseEntity<Unit> {
        val savedSurvey = surveyService.createSurvey(dto)
        // Return path to newly created resource in Location header
        val location = ucb
            .path("api/surveys/{id}")
            .buildAndExpand(savedSurvey.id)
            .toUri()

        return ResponseEntity.created(location).build()
    }
}