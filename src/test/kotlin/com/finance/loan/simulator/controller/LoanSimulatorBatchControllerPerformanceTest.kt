package com.finance.loan.simulator.controller

import com.finance.loan.simulator.LoanFlowSimulatorApp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

const val MAX_TIME_BATCH_IN_SECONDS = 2L

const val BATCH_SIMULATIONS = 30000

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [LoanFlowSimulatorApp::class]
)
@AutoConfigureWebTestClient
class LoanSimulatorBatchControllerPerformanceTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun batchSimulationPerformanceTest() {
        val requests = mutableListOf<com.finance.loan.simulator.model.LoanScenario>()
        var count = 0
        for (i in 1..BATCH_SIMULATIONS) {
            requests.add(
                com.finance.loan.simulator.model.LoanScenario(
                    loanValue = BigDecimal("100" + count),
                    birthDate = LocalDate.of(2000, 1, 1),
                    loanDurationMonths = 100
                )
            )
            count++
        }

        val startTime = LocalDateTime.now()

        webTestClient.post()
            .uri("/api/loans/simulate-batch")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk

        val endTime = LocalDateTime.now()

        val timeToExecute = Duration.between(startTime, endTime).seconds

        println("Time to execute: $timeToExecute")
        assertThat(requests.size).isEqualTo(BATCH_SIMULATIONS)
        assertThat(timeToExecute).isLessThan(MAX_TIME_BATCH_IN_SECONDS)
    }
}
