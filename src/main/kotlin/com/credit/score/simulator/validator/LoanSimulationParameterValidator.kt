package com.credit.score.simulator.validator

import com.credit.score.simulator.model.LoanSimulationParameter
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.math.BigDecimal.ONE
import java.time.Clock
import java.time.LocalDate
import java.util.*

interface LoanSimulationParameterValidator {
    suspend fun validate(parameter: LoanSimulationParameter)
}

@Component
class LoanSimulationParameterValidatorImpl(
    val clock: Clock,
    val messageSource: MessageSource
) : LoanSimulationParameterValidator {

    override suspend fun validate(parameter: LoanSimulationParameter) {
        if (parameter.birthDate.isAfter(LocalDate.now(clock))) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("error.birthdate.future", null, Locale.getDefault())
            )
        }
        if (parameter.loanValue < ONE) {
            throw LoanSimulationParameterException(
                messageSource.getMessage(
                    "error.min.loan.value", null, Locale.getDefault()
                )
            )
        }
        if (parameter.paymentTermInMonths <= 0) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("error.min.months.number", null, Locale.getDefault())
            )
        }
    }
}

class LoanSimulationParameterException(message: String) : IllegalStateException(message)