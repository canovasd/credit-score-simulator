package com.finance.loan.simulator.actor

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AgeCalculatorTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2025-03-09T10:00:00Z"),
        ZoneId.of("UTC")
    )

    @Test
    fun calculateOneYear() {
        val birthDate = LocalDate.of(2024, 3, 9)

        val target = AgeCalculatorImpl(fixedClock)

        val result = runBlocking { target.calculateAge(birthDate) }

        assertThat(result).isEqualTo(1)
    }

    @Test
    fun calculateZeroYears() {
        val birthDate = LocalDate.of(2024, 4, 11)
        val target = AgeCalculatorImpl(fixedClock)

        val result = runBlocking { target.calculateAge(birthDate) }

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun calculate40Years() {
        val birthDate = LocalDate.of(1985, 1, 19)
        val target = AgeCalculatorImpl(fixedClock)

        val result = runBlocking { target.calculateAge(birthDate) }

        assertThat(result).isEqualTo(40)
    }
}
