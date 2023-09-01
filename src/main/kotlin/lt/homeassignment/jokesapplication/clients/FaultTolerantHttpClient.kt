package lt.homeassignment.jokesapplication.clients

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.retry.Retry
import lt.homeassignment.jokesapplication.model.JokeApiBadRequestException
import lt.homeassignment.jokesapplication.model.JokeApiException
import lt.homeassignment.jokesapplication.model.JokeApiRateLimitException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import java.time.Clock
import java.time.Instant

// Create an interface for the retry logic
interface RetryHandler {
    fun <T> executeWithRetry(supplier: () -> T): T
}

// Decided to extract the retry logic to a separate class as it's configurable component and makes unit test too complicated
// BDD tests will cover actual retrying logic
@Component
class RealRetryHandler(private val retry: Retry, private val circuitBreaker: CircuitBreaker) : RetryHandler {
    override fun <T> executeWithRetry(supplier: () -> T): T {
        return Retry.decorateCheckedSupplier(
            retry,
            CircuitBreaker.decorateCheckedSupplier(circuitBreaker, supplier)
        ).apply()
    }
}


@Component
class FaultTolerantHttpClient(
    private val restOperations: RestOperations,
    private val retryHandler: RetryHandler,
    private val clock: Clock // Inject a Clock for easier testing
) : CommonHttpClient {

    private val logger = KotlinLogging.logger {}
    private var nextAllowedRequestTime: Instant = Instant.MIN // Initialize with a time in the past

    override fun <T> executeRequest(url: String, responseType: Class<T>): T {
        if (clock.instant().isBefore(nextAllowedRequestTime)) {
            throw JokeApiRateLimitException("Rate limit exceeded. Try again later.")
        }

        return retryHandler.executeWithRetry {
                val response = restOperations.getForEntity(url, responseType)
                logUrlAndStatusCode(url, response)
                handleResponseStatus(url, response)

                response.body ?: throw JokeApiBadRequestException("API responded with empty body")
            }
    }

    private fun <T> handleResponseStatus(url: String, response: ResponseEntity<T>) {
        when {
            response.statusCode.is2xxSuccessful -> return
            response.statusCode.is4xxClientError -> {
                handleRateLimit(response)
                throw JokeApiRateLimitException("Rate limit exceeded for $url.")
            }
            else -> throw JokeApiException("API responded with status code: ${response.statusCode}")
        }
    }

    private fun <T> handleRateLimit(response: ResponseEntity<T>) {
        // For this exercise we will only handle the Retry-After header and ignore the X-RateLimit-Reset header
        // Also we will not handle the case when the Retry-After header is not a number
        // I might want to add logic to handle other headers like X-RateLimit-Remaining
        val retryAfterHeader = response.headers["Retry-After"]?.firstOrNull()?.toLongOrNull()
        retryAfterHeader?.let {
            nextAllowedRequestTime = clock.instant().plusSeconds(it)
            logger.warn("Rate limit exceeded. Should retry after $it seconds.")
        }
    }

    private fun <T> logUrlAndStatusCode(url: String, response: ResponseEntity<T>) {
        logger.info("Request to: $url | Status Code: ${response.statusCode}")
    }
}
