package com.finance.loan.simulator.actor

import com.finance.loan.simulator.queue.QueueSender
import com.finance.loan.simulator.model.LoanSimulation
import com.finance.loan.simulator.model.SimulationToSend
import org.springframework.stereotype.Component

interface ResultNotifier {
    fun sendToQueue(email: String, result: LoanSimulation)
}

/**
 * Classe que simula envio de resultado para uma fila
 */
@Component
class ResultNotifierImpl(
    private val queueSender: QueueSender
) : ResultNotifier {
    override fun sendToQueue(email: String, result: LoanSimulation) {
        queueSender.sendToQueue(
            SimulationToSend(result, email),
            "queue-mock-url"
        )
    }
}
