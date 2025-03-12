package com.credit.score.simulator.validator

import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.validator.LoanSimulationParameterException
import com.credit.score.simulator.validator.LoanSimulationParameterValidatorImpl
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
import java.util.*

class LoanSimulatorParameterValidatorTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2025-03-03T10:00:00Z"),
        ZoneId.of("UTC")
    )

    private val messageSource = mock<MessageSource>()

    @Test
    fun validParameter() {
        val validParam = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 12
        )
        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource)

        try {
            runBlocking { target.validate(validParam) }
        } catch (e: Exception) {
            Assertions.fail("Shouldn't throw Exception. Valid parameter")
        }
    }

    @Test
    fun blockZeroLoanValue() {
        val param = LoanSimulationParameter(
            loanValue = ZERO,
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 12
        )
        val msg = "Informe um valor de empréstimo maior ou igual 1"
        whenever(messageSource.getMessage(
            "error.min.loan.value", null, Locale.getDefault())
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource)

        try {
            runBlocking { target.validate(param) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockNegativeLoanValue() {
        val param = LoanSimulationParameter(
            loanValue = BigDecimal("-1"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 12
        )
        val msg = "Informe um valor de empréstimo maior ou igual 1"
        whenever(messageSource.getMessage(
            "error.min.loan.value", null, Locale.getDefault())
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource)

        try {
            runBlocking { target.validate(param) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockFutureBirthDate() {
        val param = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2026, 3, 3),
            paymentTermInMonths = 12
        )
        val msg = "Data de nascimento não pode ser futura"
        whenever(messageSource.getMessage(
            "error.birthdate.future", null, Locale.getDefault())
        ).thenReturn(msg)
        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource)

        try {
            runBlocking { target.validate(parameter = param) }
            Assertions.fail("Should throw exception")
        } catch (e: LoanSimulationParameterException) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockZeroMonths() {
        val validParam = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 0
        )
        val msg = "Informe um número positivo de meses"
        whenever(messageSource.getMessage(
            "error.min.months.number", null, Locale.getDefault())
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }

    @Test
    fun blockNegativeMonths() {
        val validParam = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = -1
        )
        val msg = "Informe um número positivo de meses"
        whenever(messageSource.getMessage(
            "error.min.months.number", null, Locale.getDefault())
        ).thenReturn(msg)

        val target = LoanSimulationParameterValidatorImpl(fixedClock, messageSource)

        try {
            runBlocking { target.validate(validParam) }
            Assertions.fail("Should throw exception")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo(msg)
        }
    }
}