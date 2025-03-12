package com.credit.score.simulator.controller

import com.credit.score.simulator.CreditScoreSimulatorApp
import com.credit.score.simulator.model.LoanSimulationParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [CreditScoreSimulatorApp::class]
)
@AutoConfigureWebTestClient
class LoanSimulatorBatchControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun testBatchSimulation() {
        val requests = listOf(
            LoanSimulationParameter(
                loanValue = BigDecimal("10000"),
                birthDate = LocalDate.of(2000, 1, 1),
                paymentTermInMonths = 60
            ),
            LoanSimulationParameter(
                loanValue = BigDecimal("-1"),
                birthDate = LocalDate.of(2025, 1, 1),
                paymentTermInMonths = 0
            )
        )

        val result = webTestClient.post()
            .uri("/api/loans/simulate-batch")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .returnResult()

        assertThat(result.responseBody?.decodeToString()).isEqualTo(
            "[" +
                    "{\"loanSimulation\"" +
                    ":{\"installmentRate\":188.71," +
                    "\"finalValue\":11322.60," +
                    "\"totalInterest\":1322.60," +
                    "\"yearlyRate\":0.05}" +
                    "}," +
                    "{\"errorMessage\":\"Informe um valor de empréstimo maior ou igual 1\"}" +
                    "]"
        )
    }
}