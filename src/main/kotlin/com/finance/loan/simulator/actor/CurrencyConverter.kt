package com.finance.loan.simulator.actor

import com.finance.loan.simulator.model.Currency
import org.springframework.stereotype.Component
import java.math.BigDecimal

interface CurrencyConverter {
    fun convert(
        amount: BigDecimal,
        from: Currency?,
        to: Currency?
    ): BigDecimal
}

/**
 * Classe que converte moedas, com valores cravados para as cotações
 * Em um cenário real, implementar alguma ferramenta que pegue esse valor em tempo real
 * e saiba cachear corretamente
 */
@Component
class CurrencyConverterImpl : CurrencyConverter {
    override fun convert(amount: BigDecimal, from: Currency?, to: Currency?): BigDecimal {
        if (to == null || from == null) {
            return amount
        }
        val amountInBRL = amount * from.conversionRateToBRL
        return amountInBRL / to.conversionRateToBRL
    }
}
