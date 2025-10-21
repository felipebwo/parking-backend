package com.parking.service

import com.parking.model.Revenue
import com.parking.model.Sector
import com.parking.repository.RevenueRepository
import com.parking.repository.SectorRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals

class WebhookServiceTest {

    private val revenueRepo: RevenueRepository = mock()
    private val sectorRepo: SectorRepository = mock()
    private lateinit var service: RevenueService

    @BeforeEach
    fun setup() {
        service = RevenueService(revenueRepo, sectorRepo)
    }

    @Test
    fun `should insert new revenue when no existing record (simulating EXIT webhook)`() {
        // Simula um setor existente
        val sector = Sector(id = 1, garageId = 1, name = "A", basePrice = BigDecimal.TEN)
        whenever(sectorRepo.findById(1)).thenReturn(Optional.of(sector))
        whenever(revenueRepo.findBySectorAndDate(eq("A"), any())).thenReturn(null)

        // Simula o exit_time recebido de um webhook DTO
        val exitTimeIso = "2025-10-21T10:00:00Z"
        val instant = Instant.parse(exitTimeIso)

        service.recordRevenue(1, instant, BigDecimal(100))

        verify(revenueRepo).save(check {
            assertEquals("A", it.sector)
            assertEquals(BigDecimal(100), it.amount)
            assertEquals(LocalDate.ofInstant(instant, ZoneOffset.UTC), it.date)
            assertEquals("BRL", it.currency)
        })
    }

    @Test
    fun `should update existing revenue amount for same sector and date`() {
        val sector = Sector(id = 1, garageId = 1, name = "B", basePrice = BigDecimal.TEN)
        val today = LocalDate.now(ZoneOffset.UTC)
        val existing = Revenue(id = 1, sector = "B", date = today, amount = BigDecimal(50))

        whenever(sectorRepo.findById(1)).thenReturn(Optional.of(sector))
        whenever(revenueRepo.findBySectorAndDate("B", today)).thenReturn(existing)

        val newAmount = BigDecimal(30)
        val timestamp = today.atStartOfDay().toInstant(ZoneOffset.UTC)

        service.recordRevenue(1, timestamp, newAmount)

        verify(revenueRepo).save(check {
            assertEquals(BigDecimal(80), it.amount)
            assertEquals(today, it.date)
        })
    }

    @Test
    fun `should sum all sectors when querying without sector`() {
        val date = LocalDate.now(ZoneOffset.UTC)
        val revenues = listOf(
            Revenue(id = 1, sector = "A", date = date, amount = BigDecimal(25)),
            Revenue(id = 2, sector = "B", date = date, amount = BigDecimal(35))
        )
        whenever(revenueRepo.findByDate(date)).thenReturn(revenues)

        val total = service.queryRevenue(null, date.toString())

        assertEquals(BigDecimal(60), total)
    }

    @Test
    fun `should return zero when no revenue found for given sector and date`() {
        val date = LocalDate.now(ZoneOffset.UTC)
        whenever(revenueRepo.findBySectorAndDate("C", date)).thenReturn(null)

        val result = service.queryRevenue("C", date.toString())

        assertEquals(BigDecimal.ZERO, result)
    }

    @Test
    fun `should return specific revenue when sector provided`() {
        val date = LocalDate.now(ZoneOffset.UTC)
        val revenue = Revenue(id = 1, sector = "A", date = date, amount = BigDecimal(150))
        whenever(revenueRepo.findBySectorAndDate("A", date)).thenReturn(revenue)

        val result = service.queryRevenue("A", date.toString())

        assertEquals(BigDecimal(150), result)
    }
}
