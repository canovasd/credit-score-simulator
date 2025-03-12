package com.finance.loan.simulator.Queue

import com.finance.loan.simulator.model.SimulationToSend
import org.springframework.stereotype.Component

interface QueueSender {
    fun sendToQueue(simulationToSend: SimulationToSend, queueUrl: String)
}

/**
 * Classe que, em uma implementação com filas, enviaria para uma fila uma mensagem
 */
@Component
class QueueSenderImpl(
    val mockQueue: InMemoryMockQueue
) : QueueSender {
    override fun sendToQueue(simulationToSend: SimulationToSend, queueUrl: String) {
        mockQueue.simulations.add(simulationToSend)
    }
}