package com.parking.repository

import com.parking.model.ParkingSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParkingSessionRepository : JpaRepository<ParkingSession, Long> {
    fun findByLicensePlateAndExitTimeIsNull(licensePlate: String): ParkingSession?
}
