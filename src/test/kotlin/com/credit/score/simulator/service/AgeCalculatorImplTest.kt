package com.credit.score.simulator.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AgeCalculatorImplTest {

    @Test
    fun calculateOneYear() {
        val today = LocalDate.of(2025, 3, 11)
        val birthDate = LocalDate.of(2024, 3, 11)
        val target = AgeCalculatorImpl()

        val result = runBlocking { target.calculateAge(birthDate, today) }

        assertThat(result).isEqualTo(1)
    }

    @Test
    fun calculateZeroYears() {
        val today = LocalDate.of(2025, 3, 11)
        val birthDate = LocalDate.of(2024, 4, 11)
        val target = AgeCalculatorImpl()

        val result = runBlocking { target.calculateAge(birthDate, today) }

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun calculate40Years() {
        val today = LocalDate.of(2025, 3, 11)
        val birthDate = LocalDate.of(1985, 1, 19)
        val target = AgeCalculatorImpl()

        val result = runBlocking { target.calculateAge(birthDate, today) }

        assertThat(result).isEqualTo(40)
    }
}