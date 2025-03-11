package com.credit.score.simulator.service

import com.credit.score.simulator.model.LoanSimulation
import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.model.LoanSimulationResult
import com.credit.score.simulator.model.LoanSimulationResult.LoanSimulationFail
import com.credit.score.simulator.model.LoanSimulationResult.LoanSimulationSuccess
import com.credit.score.simulator.service.calculator.InstallmentRateCalculator
import com.credit.score.simulator.service.calculator.RateCalculator
import com.credit.score.simulator.service.validator.LoanSimulationParameterException
import com.credit.score.simulator.service.validator.LoanSimulationParameterValidator
import org.springframework.stereotype.Component
import java.math.BigDecimal

const val MONETARY_SCALE = 2

@Component
class LoanSimulatorService(
    val rateCalculator: RateCalculator,
    val installmentRateCalculator: InstallmentRateCalculator,
    val paramValidator: LoanSimulationParameterValidator
) {
    suspend fun calculateScore(param: LoanSimulationParameter): LoanSimulationResult {
        try {
            paramValidator.validate(param)
        } catch (e: LoanSimulationParameterException) {
            return LoanSimulationFail(
                errorMessage = e.message
            )
        }

        val rate = rateCalculator.calculateYearlyRate(param.birthDate)

        val installmentRate = installmentRateCalculator.calculateScore(
            param.loanValue,
            rate.monthlyRate,
            param.paymentTermInMonths
        )
        val finalValue = installmentRate * BigDecimal(param.paymentTermInMonths)
        val totalInterest = finalValue - param.loanValue

        return LoanSimulationSuccess(
            LoanSimulation(
                installmentRate = installmentRate,
                totalInterest = totalInterest,
                finalValue = finalValue,
                yearlyRate = rate.yearlyRate
            )
        )
    }
}
