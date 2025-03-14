package com.finance.loan.simulator.validator

import com.finance.loan.simulator.actor.AgeCalculatorImpl
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.time.Clock
import java.time.LocalDate
import java.util.Locale

const val MINIMAL_AGE = 18
const val MAX_MONTHS = 1500
const val MAX_VALUE = "99999999"

interface LoanSimulationParameterValidator {
    suspend fun validate(parameter: com.finance.loan.simulator.model.LoanScenario)
}

@Component
class LoanSimulationParameterValidatorImpl(
    val clock: Clock,
    val messageSource: MessageSource,
    val ageCalculatorImpl: AgeCalculatorImpl
) : LoanSimulationParameterValidator {

    override suspend fun validate(parameter: com.finance.loan.simulator.model.LoanScenario) {
        if (parameter.birthDate.isAfter(LocalDate.now(clock))) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("error.birthdate.future", null, Locale.getDefault())
            )
        }
        if (parameter.loanValue < ONE) {
            throw LoanSimulationParameterException(
                messageSource.getMessage(
                    "error.min.loan.value",
                    null,
                    Locale.getDefault()
                )
            )
        }
        if (parameter.loanDurationMonths <= 0) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("error.min.months.number", null, Locale.getDefault())
            )
        }

        if (ageCalculatorImpl.calculateAge(parameter.birthDate) < MINIMAL_AGE) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("too.young.to.receive.loan", null, Locale.getDefault())
            )
        }

        if (parameter.loanDurationMonths > MAX_MONTHS) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("loan.duration.too.long", null, Locale.getDefault())
            )
        }

        if (parameter.loanValue > BigDecimal(MAX_VALUE)) {
            throw LoanSimulationParameterException(
                messageSource.getMessage("loan.value.too.high", null, Locale.getDefault())
            )
        }
    }
}

class LoanSimulationParameterException(message: String) : IllegalStateException(message)
