package com.finance.loan.simulator.model

import java.math.BigDecimal

enum class Currency(
    val conversionRateToBRL: BigDecimal
) {
    BRL(BigDecimal.ONE),
    USD(BigDecimal("5.5")),
    EUR(BigDecimal("6.0")),
    JPY(BigDecimal("0.05")),
    CNY(BigDecimal("0.85"))
}
