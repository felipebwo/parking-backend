package com.parking.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "parking_sessions")
data class ParkingSession(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val licensePlate: String = "",

    var spotId: Long? = null,
    var sectorId: Long? = null,

    var entryTime: Instant? = null,
    var parkedTime: Instant? = null,
    var exitTime: Instant? = null,

    var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "dynamic_multiplier", precision = 5, scale = 2)
    var dynamicMultiplier: BigDecimal? = null
)
