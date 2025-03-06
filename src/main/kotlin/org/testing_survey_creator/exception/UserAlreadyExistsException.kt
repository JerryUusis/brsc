package org.testing_survey_creator.exception

class UserAlreadyExistsException(message: String = "User already exists") : RuntimeException(message)