package com.example.petshopkotlin

import org.springframework.http.ResponseEntity
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import javax.naming.AuthenticationException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException) = ResponseEntity
        .badRequest()
        .body(ex.message)

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException) = ResponseEntity
        .status(401)
        .body("You must login to perform this action")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAuthorizationException(ex: AuthorizationServiceException) = ResponseEntity
        .status(403)
        .body("You are not authorized to perform this action")

    @ExceptionHandler(InternalServerError::class)
    fun handleException(ex: Exception) = ResponseEntity
        .internalServerError()
        .body(ex.message)
}