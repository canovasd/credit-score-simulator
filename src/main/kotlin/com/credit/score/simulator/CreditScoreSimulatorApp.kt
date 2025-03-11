package com.credit.score.simulator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.credit.score.simulator"])
class CreditScoreSimulatorApp

fun main(args: Array<String>) {
    runApplication<CreditScoreSimulatorApp>(*args)
}
