package com.parking.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookDto(
    val license_plate: String?,
    val entry_time: String?,
    val exit_time: String?,
    val lat: Double? = null,
    val lng: Double? = null,
    val event_type: String
)
