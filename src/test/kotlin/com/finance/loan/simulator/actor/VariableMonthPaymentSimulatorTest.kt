package com.finance.loan.simulator.actor

import com.finance.loan.simulator.model.FinancialIndex
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class VariableMonthPaymentSimulatorTest {

    @Test
    fun baseScenario() {
        val monthlyPaymentCalculator = mock<MonthlyPaymentCalculator>()
        val target = VariableMonthPaymentSimulator(monthlyPaymentCalculator)

        whenever(
            runBlocking {
                monthlyPaymentCalculator.calculateMonthlyPayment(any(), any(), any())
            }
        ).thenReturn(
            BigDecimal("2400")
        )

        val result = runBlocking {
            target.simulateVariableRateLoan(
                loanValue = BigDecimal("200000"),
                loanDurationMonths = 120,
                fixPartRate = BigDecimal("0.003333"),
                financialIndex = FinancialIndex.IPCA
            )
        }
        assertThat(result.loanDurationMonths).isEqualTo(120)
        assertThat(result.originalValue).isEqualTo(BigDecimal(200000))
        assertThat(result.finalValue).isEqualTo(BigDecimal(288000))
        assertThat(result.totalInterest).isEqualTo(BigDecimal(88000))
        assertThat(result.evolution.size).isEqualTo(120)
        assertThat(result.evolution.first().monthlyPayment).isEqualTo(BigDecimal(2400))
    }
}
