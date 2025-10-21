package com.parking.repository

import com.parking.model.Spot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotRepository : JpaRepository<Spot, Long> {
    fun findBySectorIdAndOccupied(sectorId: Long, occupied: Boolean): List<Spot>
}
