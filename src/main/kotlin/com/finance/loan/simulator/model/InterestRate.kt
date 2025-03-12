package com.finance.loan.simulator.model

import java.math.BigDecimal

data class InterestRate(
    val rateType: InterestRateType = InterestRateType.FIXED_RATE, // IMPLEMENTAR MUDANÇA
    val yearlyRate: BigDecimal,
    val monthlyRate: BigDecimal,
    val rateByMonth: Map<BigDecimal, Int> = mapOf() // IMPLEMENTAR
)

enum class InterestRateType { FIXED_RATE, VARIABLE_RATE }
