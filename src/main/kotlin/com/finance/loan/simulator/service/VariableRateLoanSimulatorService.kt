package com.finance.loan.simulator.service

import com.finance.loan.simulator.actor.VariableMonthPaymentSimulator
import com.finance.loan.simulator.model.VariableRateLoanScenario
import com.finance.loan.simulator.model.VariableRateLoanSimulation
import org.springframework.stereotype.Component

@Component
class VariableRateLoanSimulatorService(
    private val variableMonthPaymentSimulator: VariableMonthPaymentSimulator
) {
    suspend fun calculate(param: VariableRateLoanScenario): VariableRateLoanSimulation {
        return variableMonthPaymentSimulator.simulateVariableRateLoan(
            param.loanValue,
            param.loanDurationMonths,
            param.fixPartRate,
            param.financialIndex
        )
    }
}
