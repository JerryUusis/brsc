package org.testing_survey_creator.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.testing_survey_creator.dto.LoginDTO
import org.testing_survey_creator.service.LoginService


@RestController
class LoginController(private val loginService: LoginService) {
    @PostMapping("api/login")
    fun login(@RequestBody loginCredentials: LoginDTO): ResponseEntity<Map<String, String>> { // Return JSON
        val token = loginService.loginUser(loginCredentials)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}