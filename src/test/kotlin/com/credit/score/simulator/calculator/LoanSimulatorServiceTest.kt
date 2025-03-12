package com.credit.score.simulator.service

import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.model.LoanSimulationResult
import com.credit.score.simulator.model.Rate
import com.credit.score.simulator.calculator.InstallmentRateCalculator
import com.credit.score.simulator.calculator.RateCalculator
import com.credit.score.simulator.validator.LoanSimulationParameterException
import com.credit.score.simulator.validator.LoanSimulationParameterValidator
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class LoanSimulatorServiceTest {

    @Test
    fun successResult() {
        val rateCalculator = mock<RateCalculator>()
        val installmentRateCalculator = mock<InstallmentRateCalculator>()
        val paramValidator = mock<LoanSimulationParameterValidator>()

        whenever(runBlocking { rateCalculator.calculateYearlyRate(any()) }).thenReturn(
            Rate(yearlyRate = BigDecimal("0.03"), monthlyRate = BigDecimal("0025"))
        )
        whenever(runBlocking {
            installmentRateCalculator.calculateInstallmentRate(
                any(),
                any(),
                any()
            )
        }).thenReturn(BigDecimal("58750.26"))

        val service = LoanSimulatorService(rateCalculator, installmentRateCalculator, paramValidator)

        val result = runBlocking {
            service.calculateScore(
                LoanSimulationParameter(
                    loanValue = BigDecimal("9999999.99"),
                    birthDate = LocalDate.of(1940, 1, 1),
                    paymentTermInMonths = 222
                )
            )
        }

        assertThat(result::class).isEqualTo(LoanSimulationResult.LoanSimulationSuccess::class)
        val success = result as LoanSimulationResult.LoanSimulationSuccess
        assertThat(success.loanSimulation.yearlyRate).isEqualTo(BigDecimal("0.03"))
        assertThat(success.loanSimulation.finalValue).isEqualTo(BigDecimal("13042557.72"))
        assertThat(success.loanSimulation.totalInterest).isEqualTo(BigDecimal("3042557.73"))
        assertThat(success.loanSimulation.installmentRate).isEqualTo(BigDecimal("58750.26"))
    }

    @Test
    fun failResult() {
        val rateCalculator = mock<RateCalculator>()
        val installmentRateCalculator = mock<InstallmentRateCalculator>()
        val paramValidator = mock<LoanSimulationParameterValidator>()
        val errorMsg = "Data de nascimento não pode ser após o dia atual"

        whenever(runBlocking { paramValidator.validate(any()) }).thenThrow(
            LoanSimulationParameterException(
                errorMsg
            )
        )

        val service = LoanSimulatorService(rateCalculator, installmentRateCalculator, paramValidator)

        val result = runBlocking {
            service.calculateScore(
                LoanSimulationParameter(
                    loanValue = BigDecimal("9999999.99"),
                    birthDate = LocalDate.of(2030, 1, 1),
                    paymentTermInMonths = 222
                )
            )
        }

        assertThat(result::class).isEqualTo(LoanSimulationResult.LoanSimulationFail::class)
        val fail = result as LoanSimulationResult.LoanSimulationFail
        assertThat(fail.errorMessage).isEqualTo(errorMsg)
    }
}