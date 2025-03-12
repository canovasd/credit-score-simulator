package com.finance.loan.simulator.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.math.BigDecimal

@ConstructorBinding
@ConfigurationProperties(prefix = "app.rates")
data class RateConfig(
    val brackets: List<AgeRateBracket>
) {
    data class AgeRateBracket(
        val maxAge: Int,
        val rate: BigDecimal
    )
}
