package lt.homeassignment.jokesapplication.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)

@ControllerAdvice
class CustomErrorHandler(private val objectMapper: ObjectMapper) {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        exception: ConstraintViolationException,
        webRequest: ServletWebRequest
    ) {
        val response = webRequest.response!!
        response.status = HttpStatus.BAD_REQUEST.value()
        response.contentType = "application/json"

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = exception.message ?: "Validation error"
        )

        val json = objectMapper.writeValueAsString(errorResponse)
        response.writer.write(json)
    }
}
