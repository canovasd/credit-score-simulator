package com.finance.loan.simulator.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

@Schema(
    description = "Parâmetros para simulação de empréstimo",
    example = """{
        "loanValue": 10000.00,
        "birthDate": "2000-01-01",
        "paymentTermInMonths": 60
    }"""
)
data class LoanScenario(
    @Schema(example = "10000.00", description = "Valor solicitado do empréstimo")
    val loanValue: BigDecimal,
    @Schema(example = "2000-01-01", description = "Data de nascimento do solicitante")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val birthDate: LocalDate,
    @Schema(example = "60", description = "Prazo em meses para pagamento")
    val loanDurationMonths: Int,
    val email: String? = null
)