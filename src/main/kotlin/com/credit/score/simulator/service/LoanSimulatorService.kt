package com.credit.score.simulator.service

import com.credit.score.simulator.actor.InstallmentRateCalculator
import com.credit.score.simulator.actor.RateCalculator
import com.credit.score.simulator.actor.ResultNotifier
import com.credit.score.simulator.model.LoanSimulation
import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.model.LoanSimulationResult
import com.credit.score.simulator.model.LoanSimulationResult.LoanSimulationFail
import com.credit.score.simulator.model.LoanSimulationResult.LoanSimulationSuccess
import com.credit.score.simulator.validator.LoanSimulationParameterException
import com.credit.score.simulator.validator.LoanSimulationParameterValidator
import org.springframework.stereotype.Component
import java.math.BigDecimal

const val MONETARY_SCALE = 2

/**
 * Serviço responsável por orquestrar as calculadoras para obter informações as informações
 * relevantes de uma simulação de empréstimo
 */
@Component
class LoanSimulatorService(
    val rateCalculator: RateCalculator,
    val installmentRateCalculator: InstallmentRateCalculator,
    val paramValidator: LoanSimulationParameterValidator,
    val notifier: ResultNotifier
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

        val installmentRate = installmentRateCalculator.calculateInstallmentRate(
            param.loanValue,
            rate.monthlyRate,
            param.paymentTermInMonths
        )
        val finalValue = installmentRate * BigDecimal(param.paymentTermInMonths)
        val totalInterest = finalValue - param.loanValue

        val loanSimulation = LoanSimulation(
            installmentRate = installmentRate,
            totalInterest = totalInterest,
            finalValue = finalValue,
            yearlyRate = rate.yearlyRate,
            originalValue = param.loanValue,
            paymentTermInMonths = param.paymentTermInMonths
        )

        if (!param.email.isNullOrBlank()) {
            notifier.sendToQueue(param.email, loanSimulation)
        }

        return LoanSimulationSuccess(loanSimulation)
    }
}
