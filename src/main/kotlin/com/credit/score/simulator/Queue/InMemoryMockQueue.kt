package com.credit.score.simulator.Queue

import com.credit.score.simulator.model.SimulationToSend
import org.springframework.stereotype.Component

@Component
data class InMemoryMockQueue(
    val simulations: MutableList<SimulationToSend> = mutableListOf()
)
