package com.credit.score.simulator.service

import java.time.LocalDate

interface AgeCalculator {
    suspend fun calculateAge(birthDate: LocalDate, now: LocalDate = LocalDate.now()): Int
}