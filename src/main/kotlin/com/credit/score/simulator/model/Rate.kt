package com.credit.score.simulator.model

import java.math.BigDecimal

data class Rate(
    val rateType: RateType = RateType.FIXED_RATE,
    val rateValue: BigDecimal,
    val rateByMonth: Map<BigDecimal, Int> = mapOf()
)

enum class RateType { FIXED_RATE, VARIABLE_RATE }