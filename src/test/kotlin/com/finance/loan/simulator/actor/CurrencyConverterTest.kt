package com.finance.loan.simulator.actor

import com.finance.loan.simulator.model.Currency
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CurrencyConverterTest {

    private val target = CurrencyConverterImpl()

    private val maxDiffAllowed = BigDecimal("0.001")

    @Test
    fun calculateFromUSD() {
        val result = target.convert(BigDecimal("100"), Currency.USD, Currency.BRL)
        assertThat(result).isCloseTo(BigDecimal("550"), within(maxDiffAllowed))
    }

    @Test
    fun calculateToUSD() {
        val result = target.convert(BigDecimal("550"), Currency.BRL, Currency.USD)
        assertThat(result).isCloseTo(BigDecimal("100"), within(maxDiffAllowed))
    }

    @Test
    fun calculateFromJPY() {
        val result = target.convert(BigDecimal("100"), Currency.JPY, Currency.BRL)
        assertThat(result).isCloseTo(BigDecimal("5.00"), within(maxDiffAllowed))
    }

    @Test
    fun calculateUSDToJPY() {
        val result = target.convert(BigDecimal("100"), Currency.USD, Currency.JPY)
        assertThat(result).isCloseTo(BigDecimal("11000"), within(maxDiffAllowed))
    }

    @Test
    fun calculateFromEUR() {
        val result = target.convert(BigDecimal("100"), Currency.EUR, Currency.BRL)
        assertThat(result).isCloseTo(BigDecimal("600"), within(maxDiffAllowed))
    }

    @Test
    fun calculateFromCNY() {
        val result = target.convert(BigDecimal("100"), Currency.CNY, Currency.BRL)
        assertThat(result).isCloseTo(BigDecimal("85"), within(maxDiffAllowed))
    }
}