package com.finance.loan.simulator.actor

import com.finance.loan.simulator.model.Currency
import java.math.BigDecimal

/**
 * Dada uma configuração de conversão de moeda, converte um valor a partir dessa configuração
 */
class MonetaryConverter(
    private val currencyConverter: CurrencyConverter,
    private val inputCurrency: Currency?,
    private val outputCurrency: Currency?
) {
    fun convert(value: BigDecimal): BigDecimal {
        if(inputCurrency == outputCurrency) {
            return value
        }
        return currencyConverter.convert(value, inputCurrency, outputCurrency)
    }
}