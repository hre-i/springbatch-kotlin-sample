package com.example.batchprocessing.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "people")
data class People(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val personId: Int = -1,
    var firstName: String? = null,
    var lastName: String? = null,
)
