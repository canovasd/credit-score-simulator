package com.credit.score.simulator.actor

import com.credit.score.simulator.service.MONETARY_SCALE
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.math.MathContext
import java.math.RoundingMode.HALF_UP

const val CALC_SCALE = 16

interface InstallmentRateCalculator {
    suspend fun calculateInstallmentRate(loanValue: BigDecimal, monthlyRate: BigDecimal, paymentTermInMonths: Int): BigDecimal
}

/**
 * Calcula o valor de uma prestação, dado um valor de empréstimo, quantidade de meses de duração e uma taxa mensal
 */
@Component
class InstallmentRateCalculatorImpl : InstallmentRateCalculator {

    override suspend fun calculateInstallmentRate(
        loanValue: BigDecimal,
        monthlyRate: BigDecimal,
        paymentTermInMonths: Int
    ): BigDecimal {
        require(loanValue > ZERO) { "Valor do empréstimo precisa ser positivo" }
        require(monthlyRate >= ZERO) { "Taxa não pode ser negativa" }
        require(paymentTermInMonths > 0) { "Quantidade de meses do empréstimo precisa ser positivo" }

        val numerator = loanValue * monthlyRate

        val mathContext = MathContext(CALC_SCALE, HALF_UP)
        val factor = (ONE.add(monthlyRate)).pow(paymentTermInMonths, mathContext)
        val denominator = ONE.subtract(ONE.divide(factor, CALC_SCALE, HALF_UP))

        return numerator.divide(denominator, MONETARY_SCALE, HALF_UP)
    }
}