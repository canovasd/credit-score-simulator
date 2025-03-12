package com.finance.loan.simulator

import com.finance.loan.simulator.config.RateConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

/**
 * Classe principal. Comece executando por aqui para disponibilizar os endpoints
 */
@SpringBootApplication(scanBasePackages = ["com.finance.loan.simulator"])
@EnableConfigurationProperties(RateConfig::class)
class LoanFlowSimulatorApp

fun main(args: Array<String>) {
    runApplication<LoanFlowSimulatorApp>(*args)
}
