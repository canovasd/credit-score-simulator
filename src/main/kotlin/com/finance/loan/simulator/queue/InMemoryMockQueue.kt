package com.finance.loan.simulator.queue

import com.finance.loan.simulator.model.SimulationToSend
import org.springframework.stereotype.Component

@Component
data class InMemoryMockQueue(
    val simulations: MutableList<SimulationToSend> = mutableListOf()
)
