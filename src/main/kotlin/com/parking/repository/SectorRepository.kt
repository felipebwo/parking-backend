package com.parking.repository

import com.parking.model.Sector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorRepository : JpaRepository<Sector, Long> {
    fun findByName(name: String): Sector?
}
