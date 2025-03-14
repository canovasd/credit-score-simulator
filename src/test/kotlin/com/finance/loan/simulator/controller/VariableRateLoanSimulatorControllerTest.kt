package com.finance.loan.simulator.controller

import com.finance.loan.simulator.LoanFlowSimulatorApp
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [LoanFlowSimulatorApp::class]
)
@AutoConfigureWebTestClient
class VariableRateLoanSimulatorControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun baseCase() {
        val requestPayload = """
            {
              "loanValue": 200000,
              "loanDurationMonths": 120,
              "fixPartRate": 0.003333,
              "financialIndex": "IPCA"
            }
        """.trimIndent()

        webTestClient.post()
            .uri("/api/loans/variable-rate/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestPayload)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.finalValue").isEqualTo(396119.83)
            .jsonPath("$.totalInterest").isEqualTo(196119.83)
            .jsonPath("$.originalValue").isEqualTo(200000)
            .jsonPath("$.loanDurationMonths").isEqualTo(120)
            .jsonPath("$.evolution[0].index").isEqualTo(1)
            .jsonPath("$.evolution[0].monthlyPayment").isEqualTo(2397.02)
            .jsonPath("$.evolution[0].fixPartRate").isEqualTo(0.003333)
            .jsonPath("$.evolution[0].variablePartRate").isEqualTo(0.0031)
    }
}
