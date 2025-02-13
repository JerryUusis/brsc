package org.testing_survey_creator.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    val id: Long? = null,

    val username: String,
    val passwordHash: String,
    val email: String,

    @Column(name ="created_at", nullable = false, updatable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(), // Auto-assign timestamp at creation

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL]) // A user can have multiple roles and role can be assigned to multiple users
    @JoinTable(
        name ="user_roles", // Join table to map many-to-many relationship with columns user_id and role_id
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()
)