package com.finance.loan.simulator.actor

import com.finance.loan.simulator.model.FinancialIndex
import com.finance.loan.simulator.model.VariableRateLoanSimulation
import com.finance.loan.simulator.model.VariableRatePaymentEvolution
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

@Component
class VariableMonthPaymentSimulator(
    private val monthlyPaymentCalculator: MonthlyPaymentCalculator
) {
    suspend fun simulateVariableRateLoan(
        loanValue: BigDecimal,
        loanDurationMonths: Int,
        fixPartRate: BigDecimal,
        financialIndex: FinancialIndex
    ): VariableRateLoanSimulation {
        val evolution = mutableListOf<VariableRatePaymentEvolution>()
        var simulatedVariation = BigDecimal.ZERO
        var variableRate = financialIndex.averageMonthlyVariation
        var totalPayment = BigDecimal.ZERO
        var amortizedValue = BigDecimal.ZERO

        for (i in 1..loanDurationMonths) {
            val calculateWithValue = loanValue - amortizedValue + totalPayment
            val monthlyPayment = monthlyPaymentCalculator.calculateMonthlyPayment(
                calculateWithValue,
                fixPartRate + variableRate,
                loanDurationMonths
            )
            totalPayment += monthlyPayment
            amortizedValue += (loanValue.divide(BigDecimal(loanDurationMonths), CALC_SCALE, HALF_UP))

            evolution.add(
                VariableRatePaymentEvolution(
                    index = i,
                    monthlyPayment = monthlyPayment,
                    fixPartRate = fixPartRate,
                    variablePartRate = variableRate
                )
            )
            simulatedVariation += BigDecimal("0.00000003")
            variableRate += simulatedVariation
        }

        val totalInterest = totalPayment - loanValue

        return VariableRateLoanSimulation(
            finalValue = totalPayment,
            totalInterest = totalInterest,
            originalValue = loanValue,
            loanDurationMonths = loanDurationMonths,
            evolution = evolution
        )
    }
}
