package lt.homeassignment.jokesapplication.controller

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import java.io.IOException


@ControllerAdvice
class CustomErrorHandler {
    @ExceptionHandler(ConstraintViolationException::class)
    @Throws(IOException::class)
    fun handleConstraintViolationException(
        exception: ConstraintViolationException,
        webRequest: ServletWebRequest
    ) {
        webRequest.response!!.sendError(HttpStatus.BAD_REQUEST.value(), exception.message)
    }
}