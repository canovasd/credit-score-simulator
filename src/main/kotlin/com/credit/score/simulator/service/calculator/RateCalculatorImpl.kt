package com.credit.score.simulator.service.calculator

import com.credit.score.simulator.model.Rate
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate

const val MONTHS_IN_YEAR = "12"

interface RateCalculator {
    suspend fun calculateYearlyRate(birthDate: LocalDate): Rate
}

@Component
class RateCalculatorImpl(
    private val ageCalculator: AgeCalculator
) : RateCalculator {

    override suspend fun calculateYearlyRate(birthDate: LocalDate): Rate {
        val age = ageCalculator.calculateAge(birthDate)
        val yearlyRate = when {
            age <= 25 -> BigDecimal("0.05")
            age <= 40 -> BigDecimal("0.03")
            age <= 60 -> BigDecimal("0.02")
            else -> BigDecimal("0.04")
        }

        return Rate(
            yearlyRate = yearlyRate,
            monthlyRate = yearlyRate.divide(BigDecimal(MONTHS_IN_YEAR), SCALE, HALF_UP),
        )
    }

}