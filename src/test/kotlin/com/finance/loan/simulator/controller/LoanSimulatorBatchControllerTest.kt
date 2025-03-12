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
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [LoanFlowSimulatorApp::class]
)
@AutoConfigureWebTestClient
class LoanSimulatorBatchControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun batchSimulation() {
        val requests = listOf(
            com.finance.loan.simulator.model.LoanScenario(
                loanValue = BigDecimal("10000"),
                birthDate = LocalDate.of(2000, 1, 1),
                loanDurationMonths = 60
            ),
            com.finance.loan.simulator.model.LoanScenario(
                loanValue = BigDecimal("-1"),
                birthDate = LocalDate.of(2025, 1, 1),
                loanDurationMonths = 0
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
                    "{" +
                    "\"loanSimulation\":" +
                    "{\"monthlyPayment\":188.71," +
                    "\"finalValue\":11322.60," +
                    "\"totalInterest\":1322.60," +
                    "\"yearlyRate\":0.05," +
                    "\"originalValue\":10000," +
                    "\"loanDurationMonths\":60," +
                    "\"currency\":\"BRL\"" +
                    "}},{" +
                    "\"errorMessage\":\"Informe um valor de empréstimo maior ou igual 1\"" +
                    "}" +
                    "]"
        )
    }
}