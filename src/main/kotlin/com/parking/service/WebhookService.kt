package com.parking.service

import com.parking.dto.WebhookDto
import com.parking.model.ParkingSession
import com.parking.repository.ParkingSessionRepository
import com.parking.repository.SectorRepository
import com.parking.repository.SpotRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class WebhookService(
    private val sessionRepo: ParkingSessionRepository,
    private val spotRepo: SpotRepository,
    private val sectorRepo: SectorRepository,
    private val billingService: BillingService,
    private val revenueService: RevenueService
) {

    fun process(dto: WebhookDto) {
        when (dto.event_type.uppercase()) {
            "ENTRY" -> handleEntry(dto)
            "PARKED" -> handleParked(dto)
            "EXIT" -> handleExit(dto)
            else -> throw IllegalArgumentException("Unknown event type: ${dto.event_type}")
        }
    }

    /**
     * Regra: Ao entrar, verificar lotação e aplicar multiplicador dinâmico.
     */
    private fun handleEntry(dto: WebhookDto) {
        val license = dto.license_plate ?: throw IllegalArgumentException("license_plate required")

        // Já está dentro?
        val existing = sessionRepo.findByLicensePlateAndExitTimeIsNull(license)
        if (existing != null) throw IllegalStateException("Vehicle already in parking")

        // Verifica se há vagas disponíveis (estacionamento global)
        val totalSpots = spotRepo.count().toInt()
        val occupied = spotRepo.findAll().count { it.occupied }
        if (occupied >= totalSpots) {
            throw IllegalStateException("Garage full - entry not allowed")
        }

        val occupancyPercent = (occupied.toDouble() / totalSpots.toDouble()) * 100.0
        val multiplier = billingService.applyDynamicMultiplier(occupancyPercent)

        val entryTime = dto.entry_time?.let {
            val ldt = java.time.LocalDateTime.parse(it)
            ldt.atZone(java.time.ZoneId.of("UTC")).toInstant()
        } ?: Instant.now()

        val session = ParkingSession(
            licensePlate = license,
            entryTime = entryTime,
            dynamicMultiplier = multiplier
        )
        sessionRepo.save(session)
    }

    /**
     * Regra: Ao estacionar, ocupar vaga e associar ao setor.
     * Se setor estiver 100% lotado, não permitir.
     */
    private fun handleParked(dto: WebhookDto) {
        val license = dto.license_plate ?: throw IllegalArgumentException("license_plate required")
        val session = sessionRepo.findByLicensePlateAndExitTimeIsNull(license)
            ?: throw IllegalStateException("No open session for vehicle")

        // Procura primeira vaga livre
        val availableSpot = spotRepo.findAll().firstOrNull { !it.occupied }
            ?: throw IllegalStateException("No available spots")

        // Checa lotação por setor
        val sectorId = availableSpot.sectorId
        val sectorSpots = spotRepo.findAll().filter { it.sectorId == sectorId }
        val occupiedSectorSpots = sectorSpots.count { it.occupied }

        if (occupiedSectorSpots >= sectorSpots.size) {
            throw IllegalStateException("Sector $sectorId is full - cannot park here")
        }

        // Ocupa a vaga
        availableSpot.occupied = true
        spotRepo.save(availableSpot)

        // Atualiza sessão
        session.spotId = availableSpot.id
        session.sectorId = availableSpot.sectorId
        session.parkedTime = Instant.now() // UTC

        sessionRepo.save(session)
    }

    /**
     * Regra: Ao sair, liberar vaga, calcular valor e registrar receita.
     */
    private fun handleExit(dto: WebhookDto) {
        val license = dto.license_plate ?: throw IllegalArgumentException("license_plate required")
        val session = sessionRepo.findByLicensePlateAndExitTimeIsNull(license)
            ?: throw IllegalStateException("No open session No available spots for entryfor vehicle")

        val exitTime = dto.exit_time?.let {
            val ldt = java.time.LocalDateTime.parse(it)
            ldt.atZone(java.time.ZoneId.of("UTC")).toInstant()
        } ?: Instant.now()
        session.exitTime = exitTime

        // Recupera setor e basePrice
        val sector = session.sectorId?.let { sectorRepo.findById(it).orElse(null) }
        val basePrice = sector?.basePrice ?: BigDecimal.TEN

        // Usa o multiplicador que foi definido na ENTRADA
        val multiplier = session.dynamicMultiplier ?: BigDecimal.ONE
        val pricePerHour = basePrice.multiply(multiplier)

        // Calcula valor da sessão (primeiros 30min grátis, arredondamento para cima)
        val amount = billingService.calculateAmount(session.entryTime ?: exitTime, exitTime, pricePerHour)
        System.out.printf("Amount: $amount")
        session.amount = amount

        // Libera a vaga
        session.spotId?.let {
            spotRepo.findById(it).ifPresent { s ->
                s.occupied = false
                spotRepo.save(s)
            }
        }

        sessionRepo.save(session)

        // Registra receita
        revenueService.recordRevenue(session.sectorId, exitTime, amount)
    }
}
