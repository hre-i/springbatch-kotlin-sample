package com.example.batchprocessing.repository

import com.example.batchprocessing.model.People
import org.springframework.data.jpa.repository.JpaRepository

interface PeopleRepository : JpaRepository<People, Int>
