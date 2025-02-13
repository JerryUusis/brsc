package org.testing_survey_creator.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import org.testing_survey_creator.dto.UserRegistrationDTO
import org.testing_survey_creator.service.UserService

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @PostMapping
    fun registerUser(@RequestBody userDTO: UserRegistrationDTO, ucb: UriComponentsBuilder): ResponseEntity<Unit> {
        val savedUser = userService.createUser(userDTO)

        val location = ucb.path("/users/${savedUser.id}")
            .buildAndExpand(savedUser.id)
            .toUri()
        return ResponseEntity.created(location).build()
    }
}