package com.parking.service

import com.parking.model.Revenue
import com.parking.model.Sector
import com.parking.repository.RevenueRepository
import com.parking.repository.SectorRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Service
class RevenueService(
    private val revenueRepo: RevenueRepository,
    private val sectorRepository: SectorRepository
) {

    fun recordRevenue(sectorId: Long?, timestamp: Instant, amount: BigDecimal) {
        val findByIdSector: Optional<Sector?>? = sectorRepository.findById(sectorId)

        val date = LocalDate.ofInstant(timestamp, ZoneId.of("UTC"))
        val existing =
            findByIdSector?.let { revenueRepo.findBySectorAndDate(sector = findByIdSector.get().name, date) }

        if (existing != null) {
            val updated = existing.copy(amount = existing.amount.add(amount))
            revenueRepo.save(updated)
        } else {
            val newRev = Revenue(sector = findByIdSector?.get()?.name, date = date, amount = amount)
            revenueRepo.save(newRev)
        }
    }


    fun queryRevenue(sector: String?, dateString: String): BigDecimal {
        val date = LocalDate.parse(dateString)

        if (date != null && sector == null) {
            val list = revenueRepo.findByDate(date)
            return list.fold(BigDecimal.ZERO) { acc, rev -> acc.add(rev.amount) }
        } else {
            val revenue = revenueRepo.findBySectorAndDate(sector!!, date)
            return (revenue?.amount ?: 0) as BigDecimal
        }
    }
}
