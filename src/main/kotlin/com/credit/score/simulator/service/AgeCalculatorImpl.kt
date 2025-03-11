package com.credit.score.simulator.service

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Period

@Component
class AgeCalculatorImpl: AgeCalculator {
    override suspend fun calculateAge(birthDate: LocalDate, now: LocalDate): Int {
        return Period.between(
            birthDate,
            now
        ).years
    }
}