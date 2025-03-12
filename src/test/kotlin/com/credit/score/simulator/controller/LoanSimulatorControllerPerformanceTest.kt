package com.credit.score.simulator.controller

import com.credit.score.simulator.CreditScoreSimulatorApp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import java.time.LocalDateTime

const val MAX_TIME_IN_SECONDS = 15L

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [CreditScoreSimulatorApp::class]
)
class LoanSimulatorControllerPerformanceTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun stressTest() {
        val startTime = LocalDateTime.now()

        for (i in 0..10000) {
            baseCase()
        }
        val endTime = LocalDateTime.now()

        val timeToExecute = Duration.between(startTime, endTime).seconds

        println("Time to execute: $timeToExecute")
        assertThat(timeToExecute).isLessThan(MAX_TIME_IN_SECONDS)
    }

    private fun baseCase() {
        val requestPayload = """
                {
                  "loanValue": 10000,
                  "birthDate": "2005-01-19",
                  "paymentTermInMonths": 60
                }
            """.trimIndent()

        webTestClient.post()
            .uri("/api/loans/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestPayload)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.loanSimulation.installmentRate").isEqualTo(188.71)
            .jsonPath("$.loanSimulation.finalValue").isEqualTo(11322.6)
            .jsonPath("$.loanSimulation.totalInterest").isEqualTo(1322.6)
            .jsonPath("$.loanSimulation.yearlyRate").isEqualTo(0.05)
    }
}