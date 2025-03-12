package com.finance.loan.simulator.validator

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.math.BigDecimal.ONE
import java.time.Clock
import java.time.LocalDate
import java.util.*

interface LoanSimulationParameterValidator {
    suspend fun validate(parameter: com.finance.loan.simulator.model.LoanScenario)
}

@Component
class LoanSimulationParameterValidatorImpl(
    val clock: Clock,
    val messageSource: MessageSource
) : LoanSimulationParameterValidator {

    override suspend fun validate(parameter: com.finance.loan.simulator.model.LoanScenario) {
        if (parameter.birthDate.isAfter(LocalDate.now(clock))) {
            throw com.finance.loan.simulator.validator.LoanSimulationParameterException(
                messageSource.getMessage("error.birthdate.future", null, Locale.getDefault())
            )
        }
        if (parameter.loanValue < ONE) {
            throw com.finance.loan.simulator.validator.LoanSimulationParameterException(
                messageSource.getMessage(
                    "error.min.loan.value", null, Locale.getDefault()
                )
            )
        }
        if (parameter.loanDurationMonths <= 0) {
            throw com.finance.loan.simulator.validator.LoanSimulationParameterException(
                messageSource.getMessage("error.min.months.number", null, Locale.getDefault())
            )
        }
    }
}

class LoanSimulationParameterException(message: String) : IllegalStateException(message)