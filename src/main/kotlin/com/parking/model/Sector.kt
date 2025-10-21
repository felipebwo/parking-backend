package com.parking.model

import jakarta.persistence.*

@Entity
@Table(name = "sectors")
data class Sector(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val garageId: Long = 0,
    val name: String = "",
    val basePrice: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    val maxCapacity: Int = 0,
    var closed: Boolean = false
)
