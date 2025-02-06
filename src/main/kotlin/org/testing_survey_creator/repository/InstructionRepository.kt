package org.testing_survey_creator.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.testing_survey_creator.model.Instruction

@Repository
interface InstructionRepository : JpaRepository<Instruction, Long> {
}