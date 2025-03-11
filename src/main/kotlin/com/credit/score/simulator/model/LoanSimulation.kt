package com.credit.score.simulator.model

import java.math.BigDecimal

sealed class LoanSimulationResult {
    data class LoanSimulationSuccess(val loanSimulation: LoanSimulation) : LoanSimulationResult()
    data class LoanSimulationFail(val errorMessage: String?) : LoanSimulationResult()
}

data class LoanSimulation(
    val installmentRate: BigDecimal,
    val finalValue: BigDecimal,
    val totalInterest: BigDecimal,
    val yearlyRate: BigDecimal
)