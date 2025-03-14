package com.finance.loan.simulator.model

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class VariableRateLoanScenario(
    @Schema(example = "10000.00", description = "Valor solicitado do empréstimo")
    val loanValue: BigDecimal,
    @Schema(example = "60", description = "Prazo em meses para pagamento")
    val loanDurationMonths: Int,
    @Schema(example = "0.02", description = "Componente fixo da taxa escolhida")
    val fixPartRate: BigDecimal,
    @Schema(example = "CDI", description = "Índice de mercado que influenciará o valor do empréstimo")
    val financialIndex: FinancialIndex
)

/**
 * Representa um índice de mercado
 * Em uma implementação completa, integrar com a B3 e demais sistemas financeiros capazes de fornecer valor mês a mês
 *
 * monthlyVariationRate Representa a variação média mensal em formato decimal.
 * Exemplo: 0.0036 = 0.36%
 */
enum class FinancialIndex(val averageMonthlyVariation: BigDecimal) {
    CDI(BigDecimal("0.004")),
    TR(BigDecimal("0.0001")),
    IPCA(BigDecimal("0.0031"))
}

data class VariableRatePaymentEvolution(
    val index: Int,
    val monthlyPayment: BigDecimal,
    val fixPartRate: BigDecimal,
    val variablePartRate: BigDecimal
)
