package com.finance.loan.simulator.Queue

import com.finance.loan.simulator.model.SimulationToSend
import org.springframework.stereotype.Component

@Component
data class InMemoryMockQueue(
    val simulations: MutableList<SimulationToSend> = mutableListOf()
)
