package com.finance.loan.simulator.controller

import com.finance.loan.simulator.model.LoanScenario
import com.finance.loan.simulator.model.LoanSimulationResult
import com.finance.loan.simulator.model.LoanSimulationResult.LoanSimulationError
import com.finance.loan.simulator.model.LoanSimulationResult.LoanSimulationSuccess
import com.finance.loan.simulator.model.VariableRateLoanScenario
import com.finance.loan.simulator.model.VariableRateLoanSimulation
import com.finance.loan.simulator.service.LoanSimulatorService
import com.finance.loan.simulator.service.VariableRateLoanSimulatorService
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
    private val loanSimulatorService: LoanSimulatorService,
    private val variableRateLoanSimulatorService: VariableRateLoanSimulatorService
) {

    @Operation(
        summary = "Simula um empréstimo",
        description = "Retorna as parcelas, valor final e juros com base nos parâmetros.",
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
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
                            "email": "marciocanovas@gmail.com",
                            "inputCurrency": "BRL",
                            "inputCurrency": "USD"
                        }
                    """
                        )
                    ]
                )
            ]
        )
        @RequestBody
        param: LoanScenario
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
        description = "Executa até 100 simulações simultâneas com controle especializado de concorrência"
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
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
                                "email": "marciocanovas@gmail.com",
                                "inputCurrency": "BRL",
                                "outputCurrency": "USD"
                            },
                            {
                                "loanValue": 20000,
                                "birthDate": "1995-05-15",
                                "loanDurationMonths": 120,
                                "email": "marciocanovas@gmail.com",
                                "inputCurrency": "BRL",
                                "outputCurrency": "USD"
                            }
                        ]
                    """
                        )
                    ]
                )
            ]
        )
        @RequestBody
        requests: List<LoanScenario>
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

    @Operation(
        summary = "Simula um empréstimo com taxa de juros variável",
        description = "Calcula o valor final, juros totais e evolução mensal de um empréstimo com componente fixo + índice financeiro variável"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Simulação calculada com sucesso",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = VariableRateLoanSimulation::class),
                examples = [
                    ExampleObject(
                        name = "Exemplo IPCA",
                        value = """
                        {
                          "finalValue": 396119.83,
                          "totalInterest": 196119.83,
                          "originalValue": 200000,
                          "loanDurationMonths": 120,
                          "evolution": [
                            {
                              "index": 1,
                              "monthlyPayment": 2397.02,
                              "fixPartRate": 0.003333,
                              "variablePartRate": 0.0031
                            }
                          ]
                        }
                    """
                    )
                ]
            )
        ]
    )
    @ApiResponse(
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
                                    "errorMessage": "Parâmetros obrigatórios não preenchidos"
                                }
                            """
                    )
                ]
            )
        ]
    )
    @PostMapping("/variable-rate/simulate")
    fun simulateWithVariableRate(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = VariableRateLoanScenario::class),
                    examples = [
                        ExampleObject(
                            name = "Exemplo IPCA",
                            value = """
                            {
                              "loanValue": 200000,
                              "loanDurationMonths": 120,
                              "fixPartRate": 0.003333,
                              "financialIndex": "IPCA"
                            }
                        """
                        )
                    ]
                )
            ]
        )
        @RequestBody
        request: VariableRateLoanScenario
    ): ResponseEntity<VariableRateLoanSimulation> {
        val result = runBlocking { variableRateLoanSimulatorService.calculate(request) }
        return ResponseEntity.ok(result)
    }
}
