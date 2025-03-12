package com.credit.score.simulator

import com.credit.score.simulator.config.RateConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.credit.score.simulator"])
@EnableConfigurationProperties(RateConfig::class)
class CreditScoreSimulatorApp

fun main(args: Array<String>) {
    runApplication<CreditScoreSimulatorApp>(*args)
}
