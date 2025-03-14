package com.finance.loan.simulator.model

import java.math.BigDecimal

data class VariableRateLoanSimulation(
    val finalValue: BigDecimal,
    val totalInterest: BigDecimal,
    val originalValue: BigDecimal,
    val loanDurationMonths: Int,
    val evolution: List<VariableRatePaymentEvolution>
)
