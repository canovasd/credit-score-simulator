package com.finance.loan.simulator.actor

import com.finance.loan.simulator.service.MONETARY_SCALE
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.math.MathContext
import java.math.RoundingMode.HALF_UP

const val CALC_SCALE = 16

interface MonthlyPaymentCalculator {
    suspend fun calculateMonthlyPayment(loanValue: BigDecimal, monthlyRate: BigDecimal, loanDurationMonths: Int): BigDecimal
}

/**
 * Calcula o valor de cada prestação de um empréstimo, dado um valor de empréstimo, quantidade de meses de duração e uma taxa mensal
 */
@Component
class MonthlyPaymentCalculatorImpl : MonthlyPaymentCalculator {

    override suspend fun calculateMonthlyPayment(
        loanValue: BigDecimal,
        monthlyRate: BigDecimal,
        loanDurationMonths: Int
    ): BigDecimal {
        require(loanValue > ZERO) { "Valor do empréstimo precisa ser positivo" }
        require(monthlyRate >= ZERO) { "Taxa não pode ser negativa" }
        require(loanDurationMonths > 0) { "Quantidade de meses do empréstimo precisa ser positivo" }

        val numerator = loanValue * monthlyRate

        val mathContext = MathContext(CALC_SCALE, HALF_UP)
        val factor = (ONE.add(monthlyRate)).pow(loanDurationMonths, mathContext)
        val denominator = ONE.subtract(ONE.divide(factor, CALC_SCALE, HALF_UP))

        return numerator.divide(denominator, MONETARY_SCALE, HALF_UP)
    }
}