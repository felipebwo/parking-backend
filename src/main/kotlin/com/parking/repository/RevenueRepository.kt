package com.parking.repository

import com.parking.model.Revenue
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface RevenueRepository : JpaRepository<Revenue, Long> {
    fun findByDate(date: LocalDate): List<Revenue>
    fun findBySectorAndDate(sector: String, date: LocalDate): Revenue?
}