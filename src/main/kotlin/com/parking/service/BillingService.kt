package com.parking.service

import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import kotlin.math.ceil

@Service
class BillingService {

    fun calculateAmount(entry: Instant, exit: Instant, pricePerHour: BigDecimal): BigDecimal {
        val minutes = Duration.between(entry, exit).toMinutes()
        if (minutes <= 30) return BigDecimal.ZERO
        val hours = ((minutes + 59) / 60) // divisão inteira arredondando para cima
        return pricePerHour.multiply(BigDecimal.valueOf(hours))
    }

    fun applyDynamicMultiplier(occupancyPercent: Double): BigDecimal {
        return when {
            occupancyPercent < 25.0 -> BigDecimal.valueOf(0.9)
            occupancyPercent < 50.0 -> BigDecimal.ONE
            occupancyPercent < 75.0 -> BigDecimal.valueOf(1.10)
            else -> BigDecimal.valueOf(1.25)
        }
    }
}
