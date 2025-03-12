package com.finance.loan.simulator.model

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().toString(),
    val path: String,
    val status: Int,
    val message: String,
    val errors: List<FieldError>
) {
    data class FieldError(
        val field: String,
        val message: String,
        val rejectedValue: Any?
    )
}
