package com.parking.config

import com.parking.dto.GarageConfigDto
import com.parking.model.Garage
import com.parking.model.Sector
import com.parking.model.Spot
import com.parking.repository.GarageRepository
import com.parking.repository.SectorRepository
import com.parking.repository.SpotRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

@Component
class GarageInitializer(
    private val garageRepo: GarageRepository,
    private val sectorRepo: SectorRepository,
    private val spotRepo: SpotRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        try {
            //Get infos and start simulator
            val rest = RestTemplate()
            val url = "http://localhost:3000/garage" // simulator endpoint
            log.info("Attempting to fetch garage config from $url (simulator)")

            val resp = rest.getForObject(url, String::class.java)
            if (resp == null) {
                log.warn("No response from simulator")
                return
            }

            //print response json
            System.out.printf(resp)

            val mapper = jacksonObjectMapper()
            val cfg: GarageConfigDto = mapper.readValue(resp)

            // create a Garage root entry (one garage)
            val garage = Garage(name = "simulated-garage", basePrice = BigDecimal.ZERO, maxCapacity = cfg.spots?.size ?: 0)
            val savedGarage = garageRepo.save(garage)
            // persist sectors
            cfg.garage?.forEach { sdto ->
                val sector = Sector(
                    garageId = savedGarage.id, name = sdto.sector, basePrice = BigDecimal.valueOf(sdto.basePrice),
                    maxCapacity = sdto.maxCapacity
                )
                sectorRepo.save(sector)
            }
            // persist spots
            cfg.spots?.forEach { spotDto ->
                // try to find sector by name
                val sector = sectorRepo.findByName(spotDto.sector)
                val sectorId = sector?.id ?: 0L
                val spot = Spot(sectorId = sectorId, lat = spotDto.lat, lng = spotDto.lng, occupied = false)
                spotRepo.save(spot)
            }
            log.info("Garage configuration persisted: sectors=${cfg.garage?.size ?: 0} spots=${cfg.spots?.size ?: 0}")
        } catch (ex: Exception) {
            log.warn("Could not fetch/persist garage config from simulator: ${ex.message}")
        }
    }
}
