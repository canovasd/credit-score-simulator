package com.finance.loan.simulator.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.finance.loan.simulator.model.Currency.BRL
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

@Schema(
    description = "Parâmetros para simulação de empréstimo",
    example = """{
        "loanValue": 10000.00,
        "birthDate": "2000-01-01",
        "loanDurationMonths": 60,
        "email": "marciocanovas@gmail.com",
        "inputCurrency": "BRL",
        "inputCurrency": "USD"
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
    @Schema(example = "marciocanovas@gmail.com", description = "E-mail para receber notificações")
    val email: String? = null,
    @Schema(example = "BRL", description = "Moeda na qual os valores estão sendo informados")
    @field:JsonProperty("inputCurrency")
    val inputCurrency: Currency = BRL,
    @Schema(example = "BRL", description = "Moeda a ser considerada na resposta")
    @field:JsonProperty("outputCurrency")
    val outputCurrency: Currency = BRL
)
