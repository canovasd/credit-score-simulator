package com.credit.score.simulator.service

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class RateCalculator(
    private val ageCalculator: AgeCalculator
) {
    suspend fun calculateYearlyRate(birthDate: LocalDate): BigDecimal {
        val age = ageCalculator.calculateAge(birthDate)
        return when {
            age <= 25 -> BigDecimal("0.05")
            age <= 40 -> BigDecimal("0.03")
            age <= 60 -> BigDecimal("0.02")
            else -> BigDecimal("0.04")
        }
    }
}