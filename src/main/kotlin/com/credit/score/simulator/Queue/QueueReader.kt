package com.credit.score.simulator.Queue

import com.credit.score.simulator.model.SimulationToSend
import org.springframework.stereotype.Component

interface QueueReader {
    fun readFromQueue(queueUrl: String? = "mock-queue-url"): SimulationToSend?
}

/**
 * Classe que, em uma implementação com filas, enviaria para uma fila uma mensagem
 */
@Component
class QueueReaderImpl(
    val mockQueue: InMemoryMockQueue
) : QueueReader {
    override fun readFromQueue(queueUrl: String?): SimulationToSend? {
        return mockQueue.simulations.removeFirstOrNull()
    }
}