package com.finance.loan.simulator.service

import com.finance.loan.simulator.actor.MonthlyPaymentCalculator
import com.finance.loan.simulator.actor.InterestRateCalculator
import com.finance.loan.simulator.actor.ResultNotifier
import com.finance.loan.simulator.model.LoanSimulation
import com.finance.loan.simulator.model.LoanSimulationResult
import com.finance.loan.simulator.model.LoanSimulationResult.LoanSimulationSuccess
import org.springframework.stereotype.Component
import java.math.BigDecimal

const val MONETARY_SCALE = 2

/**
 * Serviço responsável por orquestrar as calculadoras para obter informações as informações
 * relevantes de uma simulação de empréstimo
 */
@Component
class LoanSimulatorService(
    val interestRateCalculator: InterestRateCalculator,
    val installmentRateCalculator: MonthlyPaymentCalculator,
    val paramValidator: com.finance.loan.simulator.validator.LoanSimulationParameterValidator,
    val notifier: ResultNotifier
) {
    suspend fun simulateLoan(param: com.finance.loan.simulator.model.LoanScenario): LoanSimulationResult {
        try {
            paramValidator.validate(param)
        } catch (e: com.finance.loan.simulator.validator.LoanSimulationParameterException) {
            return LoanSimulationResult.LoanSimulationError(
                errorMessage = e.message
            )
        }

        val interestRate = interestRateCalculator.calculateInterestRate(param.birthDate)

        val monthlyPayment = installmentRateCalculator.calculateMonthlyPayment(
            param.loanValue,
            interestRate.monthlyRate,
            param.loanDurationMonths
        )
        val finalValue = monthlyPayment * BigDecimal(param.loanDurationMonths)
        val totalInterest = finalValue - param.loanValue

        val loanSimulation = LoanSimulation(
            monthlyPayment = monthlyPayment,
            totalInterest = totalInterest,
            finalValue = finalValue,
            yearlyRate = interestRate.yearlyRate,
            originalValue = param.loanValue,
            loanDurationMonths = param.loanDurationMonths
        )

        if (!param.email.isNullOrBlank()) {
            notifier.sendToQueue(param.email, loanSimulation)
        }

        return LoanSimulationSuccess(loanSimulation)
    }
}
