package com.credit.score.simulator.model

import java.math.BigDecimal

data class Rate(
    val rateType: RateType = RateType.FIXED_RATE, //IMPLEMENTAR MUDANÇA
    val yearlyRate: BigDecimal,
    val monthlyRate: BigDecimal,
    val rateByMonth: Map<BigDecimal, Int> = mapOf() //IMPLEMENTAR
)

enum class RateType { FIXED_RATE, VARIABLE_RATE }