package com.parking.model

import jakarta.persistence.*

@Entity
@Table(name = "spots")
data class Spot(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val sectorId: Long = 0,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    var occupied: Boolean = false
)
