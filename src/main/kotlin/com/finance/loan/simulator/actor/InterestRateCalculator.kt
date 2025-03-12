package com.finance.loan.simulator.actor

import com.finance.loan.simulator.config.RateConfig
import com.finance.loan.simulator.model.InterestRate
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate

const val MONTHS_IN_YEAR = "12"

interface InterestRateCalculator {
    suspend fun calculateInterestRate(birthDate: LocalDate): InterestRate
}

/**
 * Calcula a taxa anual e mensal correta a ser aplicada, de acordo com o ano de nascimento informado
 */
@Configuration
@ConfigurationProperties(prefix = "app.rates")
class InterestRateCalculatorImpl(
    private val ageCalculator: AgeCalculator,
    private val rateConfig: RateConfig
) : InterestRateCalculator {

    override suspend fun calculateInterestRate(birthDate: LocalDate): InterestRate {
        val age = ageCalculator.calculateAge(birthDate)

        val yearlyRate = rateConfig.brackets
            .first { age <= it.maxAge }
            .rate

        return InterestRate(
            yearlyRate = yearlyRate,
            monthlyRate = yearlyRate.divide(BigDecimal(MONTHS_IN_YEAR), CALC_SCALE, HALF_UP),
        )
    }
}