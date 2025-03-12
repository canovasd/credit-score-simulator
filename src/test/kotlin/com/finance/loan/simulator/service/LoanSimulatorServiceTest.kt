package com.finance.loan.simulator.service

import com.finance.loan.simulator.actor.*
import com.finance.loan.simulator.model.LoanSimulationResult
import com.finance.loan.simulator.model.InterestRate
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class LoanSimulatorServiceTest {

    private val interestRateCalculator = mock<InterestRateCalculator>()
    private val installmentRateCalculator = mock<MonthlyPaymentCalculator>()
    private val paramValidator = mock<com.finance.loan.simulator.validator.LoanSimulationParameterValidator>()
    private val notifier = mock<ResultNotifier>()
    private val currencyConverter = mock<CurrencyConverter>()

    private val service = LoanSimulatorService(
        interestRateCalculator,
        installmentRateCalculator,
        paramValidator,
        notifier,
        currencyConverter
    )

    @Test
    fun successResult() {
        whenever(runBlocking { interestRateCalculator.calculateInterestRate(any()) }).thenReturn(
            InterestRate(yearlyRate = BigDecimal("0.03"), monthlyRate = BigDecimal("0025"))
        )
        whenever(runBlocking {
            installmentRateCalculator.calculateMonthlyPayment(
                any(),
                any(),
                any()
            )
        }).thenReturn(BigDecimal("58750.26"))

        val result = runBlocking {
            service.simulateLoan(
                com.finance.loan.simulator.model.LoanScenario(
                    loanValue = BigDecimal("9999999.99"),
                    birthDate = LocalDate.of(1940, 1, 1),
                    loanDurationMonths = 222
                )
            )
        }

        assertThat(result::class).isEqualTo(LoanSimulationResult.LoanSimulationSuccess::class)
        val success = result as LoanSimulationResult.LoanSimulationSuccess
        assertThat(success.loanSimulation.yearlyRate).isEqualTo(BigDecimal("0.03"))
        assertThat(success.loanSimulation.finalValue).isEqualTo(BigDecimal("13042557.72"))
        assertThat(success.loanSimulation.totalInterest).isEqualTo(BigDecimal("3042557.73"))
        assertThat(success.loanSimulation.monthlyPayment).isEqualTo(BigDecimal("58750.26"))
        assertThat(success.loanSimulation.originalValue).isEqualTo(BigDecimal("9999999.99"))
        assertThat(success.loanSimulation.loanDurationMonths).isEqualTo(222)
        verify(notifier, never()).sendToQueue(any(), any())
    }

    @Test
    fun emailSent() {
        val email = "marciocanovas@gmail.com"
        whenever(runBlocking { interestRateCalculator.calculateInterestRate(any()) }).thenReturn(
            InterestRate(yearlyRate = BigDecimal("0.03"), monthlyRate = BigDecimal("0025"))
        )
        whenever(runBlocking {
            installmentRateCalculator.calculateMonthlyPayment(
                any(),
                any(),
                any()
            )
        }).thenReturn(BigDecimal("58750.26"))

        val result = runBlocking {
            service.simulateLoan(
                com.finance.loan.simulator.model.LoanScenario(
                    loanValue = BigDecimal("9999999.99"),
                    birthDate = LocalDate.of(1940, 1, 1),
                    loanDurationMonths = 222,
                    email = email
                )
            )
        }

        verify(notifier, times(1))
            .sendToQueue(email, (result as LoanSimulationResult.LoanSimulationSuccess).loanSimulation)
    }

    @Test
    fun failResult() {
        val errorMsg = "Data de nascimento não pode ser após o dia atual"

        whenever(runBlocking { paramValidator.validate(any()) }).thenThrow(
            com.finance.loan.simulator.validator.LoanSimulationParameterException(
                errorMsg
            )
        )

        val result = runBlocking {
            service.simulateLoan(
                com.finance.loan.simulator.model.LoanScenario(
                    loanValue = BigDecimal("9999999.99"),
                    birthDate = LocalDate.of(2030, 1, 1),
                    loanDurationMonths = 222
                )
            )
        }

        assertThat(result::class).isEqualTo(LoanSimulationResult.LoanSimulationError::class)
        val fail = result as LoanSimulationResult.LoanSimulationError
        assertThat(fail.errorMessage).isEqualTo(errorMsg)
    }
}