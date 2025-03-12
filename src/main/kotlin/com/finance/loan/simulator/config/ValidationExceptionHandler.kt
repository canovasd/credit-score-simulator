package com.finance.loan.simulator.config

import com.finance.loan.simulator.model.ErrorResponse
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ValidationExceptionHandler(
    private val messageSource: MessageSource
) {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { error ->
            ErrorResponse.FieldError(
                field = error.field,
                message = messageSource.getMessage(error, LocaleContextHolder.getLocale()),
                rejectedValue = error.rejectedValue ?: "null"
            )
        }

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                path = (request as ServletWebRequest).request.requestURI,
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Erro de validação nos campos",
                errors = errors
            )
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.constraintViolations.map { violation ->
            ErrorResponse.FieldError(
                field = violation.propertyPath.toString(),
                message = violation.message,
                rejectedValue = violation.invalidValue
            )
        }

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                path = (request as ServletWebRequest).request.requestURI,
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Erro de validação nos parâmetros",
                errors = errors
            )
        )
    }
}
