package com.github.toastshaman.springbootmasterclass.config

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors
import java.util.stream.Stream

@ControllerAdvice
internal class ExceptionControllerAdvice : ResponseEntityExceptionHandler() {

    public override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val fieldErrors = ex.bindingResult.fieldErrors
            .map { "${it.field}: ${it.defaultMessage}" }

        val globalErrors = ex.bindingResult.globalErrors
            .map { "${it.objectName}: ${it.defaultMessage}" }

        val errors = fieldErrors + globalErrors
        val apiError = ApiError(BAD_REQUEST, ex.localizedMessage, errors)

        return handleExceptionInternal(ex, apiError, headers, apiError.status, request)
    }
}

class ApiError(
    val status: HttpStatus,
    val message: String,
    val errors: List<String>
)