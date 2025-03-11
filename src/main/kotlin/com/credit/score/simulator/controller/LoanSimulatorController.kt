package com.credit.score.simulator.controller

import com.credit.score.simulator.model.LoanSimulationParameter
import com.credit.score.simulator.model.LoanSimulationResult
import com.credit.score.simulator.service.LoanSimulatorService
import kotlinx.coroutines.runBlocking
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate

@Component
@RestController
@RequestMapping("/api/loan")
class LoanSimulatorControllerController(
    private val loanSimulatorService: LoanSimulatorService,
) {

    @GetMapping
    fun getLoan(
        @RequestParam("loan-value") loanValue: BigDecimal,
        @RequestParam("birth-date") @DateTimeFormat(pattern = "yyyy-MM-dd") birthDate: LocalDate,
        @RequestParam("payment-term-in-months") paymentTermInMonths: Int
    ): ResponseEntity<LoanSimulationResult> {
        val result = runBlocking {
            loanSimulatorService.calculateScore(
                LoanSimulationParameter(
                    loanValue = loanValue, birthDate = birthDate, paymentTermInMonths = paymentTermInMonths
                )
            )
        }
        return when (result) {
            is LoanSimulationResult.LoanSimulationSuccess -> ResponseEntity.ok(result)
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result)
        }
    }
}