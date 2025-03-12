package com.finance.loan.simulator.actor

import com.finance.loan.simulator.config.RateConfig
import com.finance.loan.simulator.config.RateConfig.AgeRateBracket
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class InterestInterestRateCalculatorTest {

    private val ageCalculator = mock<AgeCalculator>()

    private val rateConfig = RateConfig(
        listOf(
            AgeRateBracket(25, BigDecimal("0.05")),
            AgeRateBracket(40, BigDecimal("0.03")),
            AgeRateBracket(60, BigDecimal("0.02")),
            AgeRateBracket(2147483647, BigDecimal("0.04"))
        )
    )

    @Test
    fun calculateRateTwentyYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(20)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(2004, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.05"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0041666666666667"))
    }

    @Test
    fun calculateRateTwentyFiveYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(25)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(2000, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.05"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0041666666666667"))
    }

    @Test
    fun calculateRateTwentySixYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(26)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(1999, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.03"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0025000000000000"))
    }

    @Test
    fun calculateRateFortyYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(40)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(1985, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.03"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0025000000000000"))
    }

    @Test
    fun calculateRateFortyOneYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(41)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(1984, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.02"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0016666666666667"))
    }

    @Test
    fun calculateRateSixtyYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(60)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(1965, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.02"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0016666666666667"))
    }

    @Test
    fun calculateRateSixtyOneYears() {
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(61)

        val target = InterestRateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateInterestRate(LocalDate.of(1964, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.04"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0033333333333333"))
    }
}
