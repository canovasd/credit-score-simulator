package com.credit.score.simulator.service

import com.credit.score.simulator.model.LoanSimulationParameter
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ONE

@Component
class LoanSimulatorService(val rateCalculator: RateCalculator) {
    suspend fun calculateScore(param: LoanSimulationParameter): BigDecimal {
        val monthlyRate = rateCalculator.calculateYearlyRate(param.birthDate) / BigDecimal("12")

        val result = param.loanValue * monthlyRate / ONE - ONE / (ONE + monthlyRate).pow( param.paymentTermInMonths)
        return result
    }
}
