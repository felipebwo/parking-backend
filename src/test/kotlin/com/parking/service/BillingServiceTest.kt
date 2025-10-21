package com.parking.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

class BillingServiceTest {

    private val service = BillingService()

    @Test
    fun `should return zero if parked time is less than or equal to 30 minutes`() {
        val entry = Instant.now()
        val exit = entry.plus(30, ChronoUnit.MINUTES)
        val amount = service.calculateAmount(entry, exit, BigDecimal.TEN)
        assertEquals(BigDecimal.ZERO, amount)
    }

    @Test
    fun `should calculate correct hourly rate`() {
        val entry = Instant.now()
        val exit = entry.plus(125, ChronoUnit.MINUTES) // 2h5min
        val result = service.calculateAmount(entry, exit, BigDecimal(10))
        assertEquals(BigDecimal(30), result) // arredondado p/ 3h
    }

    @Test
    fun `should apply dynamic multiplier correctly`() {
        assertEquals(0, service.applyDynamicMultiplier(10.0).compareTo(BigDecimal("0.9")))
        assertEquals(0, service.applyDynamicMultiplier(40.0).compareTo(BigDecimal("1.0")))
        assertEquals(0, service.applyDynamicMultiplier(60.0).compareTo(BigDecimal("1.10")))
        assertEquals(0, service.applyDynamicMultiplier(85.0).compareTo(BigDecimal("1.25")))
    }
}
