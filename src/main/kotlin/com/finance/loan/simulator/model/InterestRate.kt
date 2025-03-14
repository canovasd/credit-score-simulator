package com.finance.loan.simulator.model

import java.math.BigDecimal

data class InterestRate(
    val yearlyRate: BigDecimal,
    val monthlyRate: BigDecimal
)
