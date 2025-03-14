package com.finance.loan.simulator.validator

import com.finance.loan.simulator.actor.AgeCalculatorImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

class LoanSimulatorParameterValidatorTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2025-03-03T10:00:00Z"),
        ZoneId.of("UTC")
    )

    private val messageSource = mock<MessageSource>()

    private val ageCalculator = AgeCalculatorImpl(fixedClock)

    @Test
    fun validParameter() {
        val validParam = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            loanDurationMonths = 12
        )
        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(validParam) }
        } catch (e: Exception) {
            Assertions.fail("Shouldn't throw Exception. Valid parameter")
        }
    }

    @Test
    fun blockZeroLoanValue() {
        val param = com.finance.loan.simulator.model.LoanScenario(
            loanValue = ZERO,
            birthDate = LocalDate.of(2000, 3, 3),
            loanDurationMonths = 12
        )
        val msg = "Informe um valor de empréstimo maior ou igual 1"
        whenever(
            messageSource.getMessage(
                "error.min.loan.value",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(param) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockNegativeLoanValue() {
        val param = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("-1"),
            birthDate = LocalDate.of(2000, 3, 3),
            loanDurationMonths = 12
        )
        val msg = "Informe um valor de empréstimo maior ou igual 1"
        whenever(
            messageSource.getMessage(
                "error.min.loan.value",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(param) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockFutureBirthDate() {
        val param = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2026, 3, 3),
            loanDurationMonths = 12
        )
        val msg = "Data de nascimento não pode ser futura"
        whenever(
            messageSource.getMessage(
                "error.birthdate.future",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)
        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(parameter = param) }
            Assertions.fail("Should throw exception")
        } catch (e: com.finance.loan.simulator.validator.LoanSimulationParameterException) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockZeroMonths() {
        val validParam = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            loanDurationMonths = 0
        )
        val msg = "Informe um número positivo de meses"
        whenever(
            messageSource.getMessage(
                "error.min.months.number",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockNegativeMonths() {
        val validParam = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            loanDurationMonths = -1
        )
        val msg = "Informe um número positivo de meses"
        whenever(
            messageSource.getMessage(
                "error.min.months.number",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockAgeTooYoung() {
        val validParam = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2015, 3, 3),
            loanDurationMonths = 5
        )
        val msg = "Apenas maiores de 18 anos podem receber empréstimos"
        whenever(
            messageSource.getMessage(
                "too.young.to.receive.loan",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockValueTooHigh() {
        val validParam = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("10000000000"),
            birthDate = LocalDate.of(2004, 3, 3),
            loanDurationMonths = 5
        )
        val msg = "Valor escolhido para a simulação de empréstimo muito alto"
        whenever(
            messageSource.getMessage(
                "loan.value.too.high",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun loanDurationTooLong() {
        val validParam = com.finance.loan.simulator.model.LoanScenario(
            loanValue = BigDecimal("2000"),
            birthDate = LocalDate.of(2004, 3, 3),
            loanDurationMonths = 1501
        )
        val msg = "Período escolhido para o empréstimo muito longo"
        whenever(
            messageSource.getMessage(
                "loan.duration.too.long",
                null,
                Locale.getDefault()
            )
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource, ageCalculator)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }
}
