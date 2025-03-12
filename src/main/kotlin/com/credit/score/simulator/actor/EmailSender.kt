package com.credit.score.simulator.actor

import com.credit.score.simulator.Queue.QueueReader
import com.credit.score.simulator.model.LoanSimulationResult
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

interface EmailSender {
    fun sendToQueue(email: String, result: LoanSimulationResult)
}

@Component
class EmailSenderImpl(
    val queueReader: QueueReader
): EmailSender {
    override fun sendToQueue(email: String, result: LoanSimulationResult) {
    }

    @PostConstruct
    fun startListening() {
        Thread {
            while (true) {
                val simulationToSend = queueReader.readFromQueue()
                simulationToSend?.let {
                    println("Simulando envio de mensagem para e-mail ${it.email} " +
                            "com infos sobre o empréstimo ${it.loanSimulation}")
                }
            }
        }.start()
    }

}