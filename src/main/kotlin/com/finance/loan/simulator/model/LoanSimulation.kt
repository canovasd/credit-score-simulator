package com.finance.loan.simulator.model

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(
    description = "Resultado da simulação (sucesso ou falha)",
    anyOf = [LoanSimulationResult.LoanSimulationSuccess::class, LoanSimulationResult.LoanSimulationError::class]
)
sealed class LoanSimulationResult {

    @Schema(
        description = "Resposta de sucesso na simulação",
        example = """{
        "loanSimulation": {
            "installmentRate": 188.71,
            "finalValue": 11322.6,
            "totalInterest": 1322.6,
            "yearlyRate": 0.05
        }
    }"""
    )
    data class LoanSimulationSuccess(
        @Schema(description = "Detalhes da simulação")
        val loanSimulation: LoanSimulation
    ) : LoanSimulationResult()

    @Schema(
        description = "Resposta de falha na simulação",
        example = """{ "errorMessage": "Data de nascimento inválida" }"""
    )
    data class LoanSimulationError(
        @Schema(description = "Mensagem de erro detalhada")
        val errorMessage: String?
    ) : LoanSimulationResult()
}

data class LoanSimulation(
    val monthlyPayment: BigDecimal,
    val finalValue: BigDecimal,
    val totalInterest: BigDecimal,
    val yearlyRate: BigDecimal,
    val originalValue: BigDecimal,
    val loanDurationMonths: Int,
)

data class SimulationToSend(
    val loanSimulation: LoanSimulation,
    val email: String
)