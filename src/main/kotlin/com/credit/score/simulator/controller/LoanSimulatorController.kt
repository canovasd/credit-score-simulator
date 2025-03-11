package com.credit.score.simulator.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
@RequestMapping("/api/loan/")
class LoanSimulatorControllerController(
//    private val loanSimulatorService: LoanSimulatorService,
) {

    @GetMapping
    fun getLoan(
//        @PathVariable("consultantId") consultantId: UUID,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("Granted!")
    }
}