package com.credit.score.simulator.controller

import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.model.LoanSimulationResult
import com.credit.score.simulator.model.LoanSimulationResult.LoanSimulationFail
import com.credit.score.simulator.model.LoanSimulationResult.LoanSimulationSuccess
import com.credit.score.simulator.service.LoanSimulatorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

const val SEMAPHORE_PERMITS = 100

/**
 * Onde são registrados os endpoints de simulação de empréstimo
 */
@Component
@RestController
@RequestMapping("/api/loans")
class LoanSimulatorController(
    private val loanSimulatorService: LoanSimulatorService,
) {

    private val semaphore = Semaphore(SEMAPHORE_PERMITS)

    @Operation(
        summary = "Simula um empréstimo",
        description = "Retorna as parcelas, valor final e juros com base nos parâmetros.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    name = "Exemplo básico com parâmetros corretos",
                    value = """
                        {
                            "loanValue": 10000.00,
                            "birthDate": "2000-01-01",
                            "paymentTermInMonths": 60
                        }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Simulação bem-sucedida",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LoanSimulationSuccess::class),
                    examples = [ExampleObject(
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
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Parâmetros inválidos",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LoanSimulationFail::class),
                    examples = [ExampleObject(
                        value = """
                            {
                                "errorMessage": "Data de nascimento não pode ser futura"
                            }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor",
                content = [Content(
                    examples = [ExampleObject(
                        value = """
                            {
                                "errorMessage": "Ocorreu um erro inesperado"
                            }
                        """
                    )]
                )]
            )
        ]
    )
    @PostMapping("/simulate")
    fun simulateLoan(
        @RequestBody param: LoanSimulationParameter
    ): ResponseEntity<LoanSimulationResult> {
        val result = runBlocking {
            loanSimulatorService.calculateScore(param)
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
            content = [Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = LoanSimulationParameter::class)),
                examples = [ExampleObject(
                    name = "Exemplo de lote",
                    value = """
                        [
                            {
                                "loanValue": 10000,
                                "birthDate": "2000-01-01",
                                "paymentTermInMonths": 60
                            },
                            {
                                "loanValue": 20000,
                                "birthDate": "1995-05-15",
                                "paymentTermInMonths": 120
                            }
                        ]
                    """
                )]
            )]
        )
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Lista de resultados das simulações",
            content = [Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = LoanSimulationResult::class)),
                examples = [ExampleObject(
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
                )]
            )]
        )
    )
    @PostMapping("/simulate-batch")
    suspend fun simulateLoanBatch(
        @RequestBody requests: List<LoanSimulationParameter>
    ): ResponseEntity<List<LoanSimulationResult>> = supervisorScope {
        val cpuDispatcher = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
        val results = coroutineScope {
            requests.map { request ->
                async(cpuDispatcher) {
                    loanSimulatorService.calculateScore(request)
                }
            }.awaitAll()
        }
        cpuDispatcher.close()

        ResponseEntity.ok(results)
    }

}