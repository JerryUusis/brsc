package org.testing_survey_creator.model
import jakarta.persistence.*

@Entity
@Table(name = "surveys")
data class Survey(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,  // Primary key for the database

    val issueNumber: Int,
    val issueLink: String,
    val taskNumber: Int,
    val taskTitle: String,

    @OneToMany(mappedBy = "survey", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var instructions: MutableList<Instruction> = mutableListOf()
)
