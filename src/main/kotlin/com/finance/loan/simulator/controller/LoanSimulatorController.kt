package com.finance.loan.simulator.controller

import com.finance.loan.simulator.model.LoanScenario
import com.finance.loan.simulator.model.LoanSimulationResult
import com.finance.loan.simulator.model.LoanSimulationResult.LoanSimulationError
import com.finance.loan.simulator.model.LoanSimulationResult.LoanSimulationSuccess
import com.finance.loan.simulator.service.LoanSimulatorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors

/**
 * Onde são registrados os endpoints de simulação de empréstimo
 */
@Component
@RestController
@RequestMapping("/api/loans")
class LoanSimulatorController(
    private val loanSimulatorService: LoanSimulatorService
) {

    @Operation(
        summary = "Simula um empréstimo",
        description = "Retorna as parcelas, valor final e juros com base nos parâmetros.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "Exemplo básico com parâmetros corretos",
                            value = """
                        {
                            "loanValue": 10000.00,
                            "birthDate": "2000-01-01",
                            "loanDurationMonths": 60,
                            "email": "marciocanovas@gmail.com"
                        }
                    """
                        )
                    ]
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Simulação bem-sucedida",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = LoanSimulationSuccess::class),
                        examples = [
                            ExampleObject(
                                value = """
                            {
                                "loanSimulation": {
                                    "installmentRate": 188.71,
                                    "finalValue": 11322.6,
                                    "totalInterest": 1322.6,
                                    "yearlyRate": 0.05
                                }
                            }
                        """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Parâmetros inválidos",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = LoanSimulationError::class),
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "errorMessage": "Data de nascimento não pode ser futura"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                value = """
                            {
                                "errorMessage": "Ocorreu um erro inesperado"
                            }
                        """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/simulate")
    fun simulateLoan(
        @RequestBody param: LoanScenario
    ): ResponseEntity<LoanSimulationResult> {
        val result = runBlocking {
            loanSimulatorService.simulateLoan(param)
        }
        return when (result) {
            is LoanSimulationSuccess -> ResponseEntity.ok(result)
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result)
        }
    }

    @Operation(
        summary = "Processa múltiplas simulações em paralelo",
        description = "Executa até 100 simulações simultâneas com controle automático de concorrência",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [
                Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = LoanScenario::class)),
                    examples = [
                        ExampleObject(
                            name = "Exemplo de lote",
                            value = """
                        [
                            {
                                "loanValue": 10000,
                                "birthDate": "2000-01-01",
                                "loanDurationMonths": 60,
                                "email": "marciocanovas@gmail.com"
                            },
                            {
                                "loanValue": 20000,
                                "birthDate": "1995-05-15",
                                "loanDurationMonths": 120,
                                "email": "marciocanovas@gmail.com"
                            }
                        ]
                    """
                        )
                    ]
                )
            ]
        )
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Lista de resultados das simulações",
            content = [
                Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = LoanSimulationResult::class)),
                    examples = [
                        ExampleObject(
                            value = """
                        [
                            {
                                "loanSimulation": {
                                    "installmentRate": 188.71,
                                    "finalValue": 11322.6,
                                    "totalInterest": 1322.6,
                                    "yearlyRate": 0.05
                                }
                            },
                            {
                                "errorMessage": "Data de nascimento inválida"
                            }
                        ]
                    """
                        )
                    ]
                )
            ]
        )
    )
    @PostMapping("/simulate-batch")
    suspend fun simulateLoanBatch(
        @RequestBody requests: List<LoanScenario>
    ): ResponseEntity<List<LoanSimulationResult>> = supervisorScope {
        val cpuDispatcher =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
        val results = coroutineScope {
            requests.map { request ->
                async(cpuDispatcher) {
                    loanSimulatorService.simulateLoan(request)
                }
            }.awaitAll()
        }
        cpuDispatcher.close()

        ResponseEntity.ok(results)
    }
}
