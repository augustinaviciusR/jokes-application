package lt.homeassignment.jokesapplication

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.ConstraintViolationException
import lt.homeassignment.jokesapplication.model.JokeApiException
import lt.homeassignment.jokesapplication.model.JokeApiRateLimitException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest

// Configure a global exception handler to handle exceptions thrown by the application
// This way we can handle exceptions thrown by the application in a single place
// And logs won't be polluted with stacktraces
@ControllerAdvice
class GlobalExceptionHandler(private val objectMapper: ObjectMapper) {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(JokeApiException::class)
    fun handleJokeApiException(ex: JokeApiException): ResponseEntity<String> {
        logger.error("JokeApiException: {}", ex.message)
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(JokeApiRateLimitException::class)
    fun handleRateLimitException(ex: JokeApiRateLimitException): ResponseEntity<String> {
        logger.warn("Rate Limit Exceeded: {}", ex.message)
        return ResponseEntity(ex.message, HttpStatus.TOO_MANY_REQUESTS)
    }

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

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        logger.error("An error occurred: {}", ex.message)
        return ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)
