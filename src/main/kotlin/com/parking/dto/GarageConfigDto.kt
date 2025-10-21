package com.parking.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GarageConfigDto(
    val garage: List<SectorDto>? = null,
    val spots: List<SpotDto>? = null
)

data class SectorDto(
    @JsonProperty("sector") val sector: String,
    @JsonProperty("base_price") val basePrice: Double,
    @JsonProperty("max_capacity") val maxCapacity: Int,
    @JsonProperty("open_hour") val openHour: String,
    @JsonProperty("close_hour") val closeHour: String,
    @JsonProperty("duration_limit_minutes") val durationLimitMinutes: Int
)

data class SpotDto(
    val id: Long,
    val sector: String,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean
)
