package com.parking.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "garages")
data class Garage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String? = null,
    val basePrice: BigDecimal? = null,
    val maxCapacity: Int = 0
)
