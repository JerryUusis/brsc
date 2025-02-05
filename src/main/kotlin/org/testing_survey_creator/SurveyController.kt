package org.testing_survey_creator

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import org.testing_survey_creator.model.Survey
import org.testing_survey_creator.repository.SurveyRepository

@RestController
@RequestMapping("/api/surveys")
class SurveyController(private val surveyRepository: SurveyRepository) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Survey>> {
        val surveys = surveyRepository.findAll()
        return ResponseEntity.ok(surveys)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") requestedId: Long): ResponseEntity<Survey> {
        val survey = surveyRepository.findById(requestedId)
        return if (survey.isPresent) {
            ResponseEntity.ok(survey.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createSurvey(@RequestBody survey: Survey, ucb: UriComponentsBuilder): ResponseEntity<Void> {
        val savedSurvey = surveyRepository.save(survey)

        // Return path to newly created resource
        val location = ucb
            .path("api/surveys/{id}")
            .buildAndExpand(savedSurvey.id)
            .toUri()

        return ResponseEntity.created(location).build()
    }
}