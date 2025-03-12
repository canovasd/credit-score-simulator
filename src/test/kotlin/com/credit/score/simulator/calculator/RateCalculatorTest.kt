package com.credit.score.simulator.calculator

import com.credit.score.simulator.config.RateConfig
import com.credit.score.simulator.config.RateConfig.AgeRateBracket
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class RateCalculatorTest {

    private val rateConfig = RateConfig(
        listOf(
            AgeRateBracket(25, BigDecimal("0.05")),
            AgeRateBracket(40, BigDecimal("0.03")),
            AgeRateBracket(60, BigDecimal("0.02")),
            AgeRateBracket(2147483647, BigDecimal("0.04")),
        )
    )

    @Test
    fun calculateRateTwentyYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(20)

        val a = BigDecimal("1")

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(2004, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.05"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0041666666666667"))
    }

    @Test
    fun calculateRateTwentyFiveYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(25)

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(2000, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.05"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0041666666666667"))
    }

    @Test
    fun calculateRateTwentySixYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(26)

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(1999, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.03"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0025000000000000"))
    }

    @Test
    fun calculateRateFortyYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(40)

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(1985, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.03"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0025000000000000"))
    }

    @Test
    fun calculateRateFortyOneYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(41)

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(1984, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.02"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0016666666666667"))
    }

    @Test
    fun calculateRateSixtyYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(60)

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(1965, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.02"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0016666666666667"))
    }

    @Test
    fun calculateRateSixtyOneYears() {
        val ageCalculator = mock<AgeCalculator>()
        whenever(runBlocking { ageCalculator.calculateAge(any()) }).thenReturn(61)

        val target = RateCalculatorImpl(ageCalculator, rateConfig)
        val result = runBlocking { target.calculateYearlyRate(LocalDate.of(1964, 1, 1)) }

        assertThat(result.yearlyRate).isEqualTo(BigDecimal("0.04"))
        assertThat(result.monthlyRate).isEqualTo(BigDecimal("0.0033333333333333"))
    }
}