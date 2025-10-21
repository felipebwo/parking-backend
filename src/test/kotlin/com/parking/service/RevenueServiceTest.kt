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
import java.util.*
import kotlin.test.assertEquals

class RevenueServiceTest {

    private val revenueRepo: RevenueRepository = mock()
    private val sectorRepo: SectorRepository = mock()
    private lateinit var service: RevenueService

    @BeforeEach
    fun setup() {
        service = RevenueService(revenueRepo, sectorRepo)
    }

    @Test
    fun `should insert new revenue when no existing record`() {
        val sector = Sector(id = 1, garageId = 1, name = "A", basePrice = BigDecimal.TEN)
        whenever(sectorRepo.findById(1)).thenReturn(Optional.of(sector))
        whenever(revenueRepo.findBySectorAndDate(eq("A"), any())).thenReturn(null)

        service.recordRevenue(1, Instant.now(), BigDecimal(100))

        verify(revenueRepo).save(check {
            assertEquals("A", it.sector)
            assertEquals(BigDecimal(100), it.amount)
            assertEquals("BRL", it.currency)
        })
    }

    @Test
    fun `should update existing revenue amount`() {
        val sector = Sector(1, 1, "B", BigDecimal.TEN)
        val existing = Revenue(1, "B", LocalDate.now(), BigDecimal(50))
        whenever(sectorRepo.findById(1)).thenReturn(Optional.of(sector))
        whenever(revenueRepo.findBySectorAndDate(eq("B"), any())).thenReturn(existing)

        service.recordRevenue(1, Instant.now(), BigDecimal(20))

        verify(revenueRepo).save(check {
            assertEquals(BigDecimal(70), it.amount)
        })
    }

    @Test
    fun `should sum all sectors when querying without sector`() {
        val date = LocalDate.now()
        val list = listOf(
            Revenue(1, "A", date, BigDecimal(10)),
            Revenue(2, "B", date, BigDecimal(20))
        )
        whenever(revenueRepo.findByDate(date)).thenReturn(list)

        val total = service.queryRevenue(null, date.toString())
        assertEquals(BigDecimal(30), total)
    }

    @Test
    fun `should return zero when no revenue found for sector`() {
        val date = LocalDate.now()
        whenever(revenueRepo.findBySectorAndDate("A", date)).thenReturn(null)

        val total = service.queryRevenue("A", date.toString())
        assertEquals(BigDecimal.ZERO, total)
    }
}
