package com.parking.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "revenue")
data class Revenue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val sector: String? = null,
    val date: LocalDate = LocalDate.now(),
    val amount: BigDecimal = BigDecimal.ZERO,
    val currency: String = "BRL"
)