package com.credit.score.simulator.model

import java.math.BigDecimal
import java.time.LocalDate

data class LoanSimulationParameter(
    val loanValue: BigDecimal,
    val birthDate: LocalDate,
    val paymentTermInMonths: Int
)
