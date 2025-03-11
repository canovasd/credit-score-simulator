package com.credit.score.simulator.service.calculator

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
        val result = runBlocking { target.calculateScore(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("188.71"))
    }

    @Test
    fun calculateInstallmentRateLongTerm() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("55555")
        val monthlyRate = BigDecimal("0.0016666667")
        val paymentTermInMonths = 320
        val result = runBlocking { target.calculateScore(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("224.14"))
    }

    @Test
    fun calculateInstallmentRateShortTerm() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("100000")
        val monthlyRate = BigDecimal("0.033333333")
        val paymentTermInMonths = 10
        val result = runBlocking { target.calculateScore(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("11923.34"))
    }

    @Test
    fun calculateInstallmentRateHighLoanValue() {
        val target = InstallmentRateCalculatorImpl()

        val loanValue = BigDecimal("9999999.99")
        val monthlyRate = BigDecimal("0.0025")
        val paymentTermInMonths = 222
        val result = runBlocking { target.calculateScore(loanValue, monthlyRate, paymentTermInMonths) }

        assertThat(result).isEqualTo(BigDecimal("58750.26"))
    }

}