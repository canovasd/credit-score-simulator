package com.finance.loan.simulator.service

import com.finance.loan.simulator.actor.VariableMonthPaymentSimulator
import com.finance.loan.simulator.model.FinancialIndex
import com.finance.loan.simulator.model.VariableRateLoanScenario
import com.finance.loan.simulator.model.VariableRateLoanSimulation
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigDecimal.ONE

class VariableRateLoanSimulatorServiceTest {

    @Test
    fun delegates() {
        val variableMonthPaymentSimulator = mock<VariableMonthPaymentSimulator>()
        val loanValue = BigDecimal("20000")
        val loanDurationMonths = 120
        val fixPartRate = BigDecimal("0.02")
        val financialIndex = FinancialIndex.IPCA
        val scenario = VariableRateLoanScenario(
            loanValue = loanValue,
            loanDurationMonths = loanDurationMonths,
            fixPartRate = fixPartRate,
            financialIndex = financialIndex
        )

        val simulation = VariableRateLoanSimulation(ONE, ONE, ONE, 1, listOf())
        whenever(
            runBlocking {
                variableMonthPaymentSimulator.simulateVariableRateLoan(
                    loanValue,
                    loanDurationMonths,
                    fixPartRate,
                    financialIndex
                )
            }
        ).thenReturn(simulation)
        val target = VariableRateLoanSimulatorService(variableMonthPaymentSimulator)
        assertThat(runBlocking { target.calculate(scenario) }).isEqualTo(simulation)
    }
}
