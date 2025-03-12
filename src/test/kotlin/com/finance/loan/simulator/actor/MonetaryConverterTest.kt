package com.finance.loan.simulator.actor

import com.finance.loan.simulator.model.Currency.BRL
import com.finance.loan.simulator.model.Currency.USD
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal.ONE

class MonetaryConverterTest {

    @Test
    fun convert() {
        val currencyConverter = mock<CurrencyConverter>()
        val mockedValue = ONE
        whenever(currencyConverter.convert(any(), any(), any())).thenReturn(mockedValue)

        val target = MonetaryConverter(currencyConverter, BRL, USD)
        assertThat(target.convert(mockedValue)).isEqualTo(mockedValue)
        verify(currencyConverter, times(1)).convert(mockedValue, BRL, USD)
    }

    @Test
    fun convertSameCurrency() {
        val currencyConverter = mock<CurrencyConverter>()
        val mockedValue = ONE
        whenever(currencyConverter.convert(any(), any(), any())).thenReturn(mockedValue)

        val target = MonetaryConverter(currencyConverter, BRL, BRL)
        assertThat(target.convert(mockedValue)).isEqualTo(mockedValue)
        verify(currencyConverter, never()).convert(any(), any(), any())
    }
}
