package lt.homeassignment.jokesapplication

import lt.homeassignment.jokesapplication.model.JokeApiException
import lt.homeassignment.jokesapplication.model.JokeApiRateLimitException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

// Configure a global exception handler to handle exceptions thrown by the application
// This way we can handle exceptions thrown by the application in a single place
// And logs won't be polluted with stacktraces
@ControllerAdvice
class GlobalExceptionHandler {

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

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        logger.error("An error occurred: {}", ex.message)
        return ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
