package com.parking.controller

import com.parking.service.RevenueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/revenue")
class RevenueController(private val revenueService: RevenueService) {

    @GetMapping
    fun getRevenue(
        @RequestParam date: String,
        @RequestParam(required = false) sector: String?
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val amount = revenueService.queryRevenue(sector, date)
            val response = mapOf(
                "date" to date,
                "amount" to amount,
                "currency" to "BRL",
                "timestamp" to Instant.now().toString()
            )
            ResponseEntity.ok(response)
        } catch (ex: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to "Invalid date format: ${ex.message}"))
        }
    }

}
