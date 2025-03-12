package com.credit.score.simulator.calculator

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class InstallmentRateCalculatorTest {

    @Test
    fun calculateInstallmentRate() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("10000")
        val monthlyRate = BigDecimal("0.004166667")
        val paymentTermInMonths = 60
        val result = runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("188.71"))
    }

    @Test
    fun calculateInstallmentRateLongTerm() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("55555")
        val monthlyRate = BigDecimal("0.0016666667")
        val paymentTermInMonths = 320
        val result = runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("224.14"))
    }

    @Test
    fun calculateInstallmentRateShortTerm() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("100000")
        val monthlyRate = BigDecimal("0.033333333")
        val paymentTermInMonths = 10
        val result = runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("11923.34"))
    }

    @Test
    fun calculateInstallmentRateHighLoanValue() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("9999999.99")
        val monthlyRate = BigDecimal("0.0025")
        val paymentTermInMonths = 222
        val result = runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("58750.26"))
    }

    @Test
    fun blockNegativeLoan() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("-1")
        val monthlyRate = BigDecimal("0.0025")
        val paymentTermInMonths = 222

        try {
            runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Valor do empréstimo precisa ser positivo")
        }
    }

    @Test
    fun blockNegativeRate() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("10")
        val monthlyRate = BigDecimal("-2")
        val paymentTermInMonths = 222

        try {
            runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Taxa não pode ser negativa")
        }
    }

    @Test
    fun blockNegativeMonths() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("21")
        val monthlyRate = BigDecimal("0.0025")
        val paymentTermInMonths = -1

        try {
            runBlocking { target.calculateInstallmentRate(loanValue, monthlyRate, paymentTermInMonths) }
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Quantidade de meses do empréstimo precisa ser positivo")
        }
    }

}