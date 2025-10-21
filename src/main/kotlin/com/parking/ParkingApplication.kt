package com.parking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ParkingApplication

fun main(args: Array<String>) {
    runApplication<ParkingApplication>(*args)
}
