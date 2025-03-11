package validator

import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.service.validator.LoanSimulationParameterValidatorImpl
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.LocalDate

class LoanSimulatorParameterValidatorTest {

    @Test
    fun validParameter() {
        val validParam = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 12
        )
        val target = LoanSimulationParameterValidatorImpl()

        try {
            runBlocking { target.validate(validParam) }
        } catch (e: Exception) {
            Assertions.fail("Não deveria lançar exceção. Parameter válido")
        }
    }

    @Test
    fun blockZeroLoanValue() {
        val param = LoanSimulationParameter(
            loanValue = ZERO,
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 12
        )
        val target = LoanSimulationParameterValidatorImpl()

        try {
            runBlocking { target.validate(param) }
            Assertions.fail("Deveria lançar exceção")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Escolha um valor de empréstimo maior ou igual 1")
        }
    }

    @Test
    fun blockNegativeLoanValue() {
        val param = LoanSimulationParameter(
            loanValue = BigDecimal("-1"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 12
        )
        val target = LoanSimulationParameterValidatorImpl()

        try {
            runBlocking { target.validate(param) }
            Assertions.fail("Deveria lançar exceção")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Escolha um valor de empréstimo maior ou igual 1")
        }
    }

    @Test
    fun blockFutureBirthDate() {
        val param = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2026, 3, 3),
            paymentTermInMonths = 12
        )
        val target = LoanSimulationParameterValidatorImpl()

        try {
            runBlocking { target.validate(parameter = param, now = LocalDate.of(2025, 3, 11)) }
            Assertions.fail("Deveria lançar exceção")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Data de nascimento não pode ser após o dia atual")
        }
    }

    @Test
    fun blockZeroMonths() {
        val validParam = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = 0
        )
        val target = LoanSimulationParameterValidatorImpl()

        try {
            runBlocking { target.validate(validParam) }
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Informe um número positivo de meses")
        }
    }

    @Test
    fun blockNegativeMonths() {
        val validParam = LoanSimulationParameter(
            loanValue = BigDecimal("1000"),
            birthDate = LocalDate.of(2000, 3, 3),
            paymentTermInMonths = -1
        )
        val target = LoanSimulationParameterValidatorImpl()

        try {
            runBlocking { target.validate(validParam) }
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Informe um número positivo de meses")
        }
    }
}