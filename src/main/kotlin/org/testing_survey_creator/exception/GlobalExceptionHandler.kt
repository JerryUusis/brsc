package org.testing_survey_creator.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URI

// https://medium.com/@aedemirsen/spring-boot-global-exception-handler-842d7143cf2a
@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(exception: UserAlreadyExistsException): ErrorResponse {
        val errorResponse = ErrorResponse.builder(
            exception,
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message),
        )
//        Using URN because no error docs are available
//        urn:<NID>:<NSS>
//        NID=namespace identifier.
//        NSS=namespace-specific string.
            .type(URI("urn:problem-type:user-already-exists"))
            .build()
        return errorResponse
    }

    @ExceptionHandler(CustomAuthException::class)
    fun handleCustomAuthException(exception: CustomAuthException): ErrorResponse {
        return ErrorResponse.builder(
            exception,
            ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.message ?: "Authentication failed")
        )
            .type(URI("urn:problem-type:auth-failure"))
            .instance(URI("/api/login"))
            .build()
    }
}