package com.credit.score.simulator.calculator

import com.credit.score.simulator.config.RateConfig
import com.credit.score.simulator.model.Rate
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate

const val MONTHS_IN_YEAR = "12"

interface RateCalculator {
    suspend fun calculateYearlyRate(birthDate: LocalDate): Rate
}

@Configuration
@ConfigurationProperties(prefix = "app.rates")
class RateCalculatorImpl(
    private val ageCalculator: AgeCalculator,
    private val rateConfig: RateConfig
) : RateCalculator {

    override suspend fun calculateYearlyRate(birthDate: LocalDate): Rate {
        val age = ageCalculator.calculateAge(birthDate)

        val yearlyRate = rateConfig.brackets
            .first { age <= it.maxAge }
            .rate

        return Rate(
            yearlyRate = yearlyRate,
            monthlyRate = yearlyRate.divide(BigDecimal(MONTHS_IN_YEAR), CALC_SCALE, HALF_UP),
        )
    }
}