package com.credit.score.simulator.service.calculator

import com.credit.score.simulator.service.MONETARY_SCALE
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.MathContext
import java.math.RoundingMode.HALF_UP

const val SCALE = 16

interface InstallmentRateCalculator {
    suspend fun calculateScore(loanValue: BigDecimal, monthlyRate: BigDecimal, paymentTermInMonths: Int): BigDecimal
}

@Component
class InstallmentRateCalculatorImpl : InstallmentRateCalculator {

    override suspend fun calculateScore(
        loanValue: BigDecimal,
        monthlyRate: BigDecimal,
        paymentTermInMonths: Int
    ): BigDecimal {
        val numerator = loanValue * monthlyRate

        val mathContext = MathContext(SCALE, HALF_UP)
        val factor = (ONE.add(monthlyRate)).pow(paymentTermInMonths, mathContext)
        val denominator = ONE.subtract(ONE.divide(factor, SCALE, HALF_UP))

        return numerator.divide(denominator, MONETARY_SCALE, HALF_UP)
    }
}