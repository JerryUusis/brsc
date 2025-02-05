package org.testing_survey_creator.model

import jakarta.persistence.*

@Entity
@Table(name = "surveys")
data class Survey(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val issueNumber: Int,

    val issueLink: String = "",

    val taskTitle: String = "",

    @ElementCollection(fetch = FetchType.EAGER) // Force eager loading on relational tables
    @CollectionTable(name = "survey_instructions", joinColumns = [JoinColumn(name = "survey_id")])
    @Column(name = "instruction")
    val instructions: List<String> = listOf(),
)
