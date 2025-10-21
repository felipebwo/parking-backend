package com.parking.controller

import com.parking.dto.WebhookDto
import com.parking.service.WebhookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class WebhookController(private val webhookService: WebhookService) {

    @PostMapping("/webhook")
    fun handleWebhook(@RequestBody dto: WebhookDto): ResponseEntity<String> {
        return try {
            System.out.printf("Received event...");
            webhookService.process(dto)
            ResponseEntity.ok("OK")
        } catch (e: IllegalStateException) {
            ResponseEntity.status(409).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
