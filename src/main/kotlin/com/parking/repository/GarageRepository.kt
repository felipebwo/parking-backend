package com.parking.repository

import com.parking.model.Garage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GarageRepository : JpaRepository<Garage, Long> {
    fun findByName(name: String): Garage?
}
