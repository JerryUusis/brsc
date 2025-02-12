package org.testing_survey_creator.model

import jakarta.persistence.*

@Entity
@Table(name = "instructions")
data class Instruction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val instructionText: String,

    @ManyToOne
    @JoinColumn(name = "survey_id")
    val survey: Survey
)