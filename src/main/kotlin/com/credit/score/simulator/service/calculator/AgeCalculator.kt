package com.credit.score.simulator.service.calculator

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Period

interface AgeCalculator {
    suspend fun calculateAge(birthDate: LocalDate, now: LocalDate = LocalDate.now()): Int
}

@Component
class AgeCalculatorImpl : AgeCalculator {
    override suspend fun calculateAge(birthDate: LocalDate, now: LocalDate): Int {
        return Period.between(
            birthDate,
            now
        ).years
    }
}