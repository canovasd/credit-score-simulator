package com.credit.score.simulator.service.validator

import com.credit.score.simulator.model.LoanSimulationParameter
import org.springframework.stereotype.Component
import java.math.BigDecimal.ONE
import java.time.LocalDate

interface LoanSimulationParameterValidator {
    suspend fun validate(parameter: LoanSimulationParameter, now: LocalDate = LocalDate.now())
}

@Component
class LoanSimulationParameterValidatorImpl : LoanSimulationParameterValidator {

    override suspend fun validate(parameter: LoanSimulationParameter, now: LocalDate) {
        if (parameter.birthDate.isAfter(now)) {
            throw LoanSimulationParameterException("Data de nascimento não pode ser após o dia atual")
        }
        if (parameter.loanValue < ONE) {
            throw LoanSimulationParameterException("Escolha um valor de empréstimo maior ou igual 1")
        }
        if (parameter.paymentTermInMonths <= 0) {
            throw LoanSimulationParameterException("Informe um número positivo de meses")
        }
    }
}

class LoanSimulationParameterException(message: String) : IllegalStateException(message)