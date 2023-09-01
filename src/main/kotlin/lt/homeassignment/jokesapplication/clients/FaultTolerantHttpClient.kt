package lt.homeassignment.jokesapplication.clients

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import jakarta.annotation.PostConstruct
import lt.homeassignment.jokesapplication.model.JokeApiBadRequestException
import lt.homeassignment.jokesapplication.model.JokeApiException
import lt.homeassignment.jokesapplication.model.JokeApiRateLimitException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.Clock
import java.time.Instant

@Component
class FaultTolerantHttpClient(
    private val restOperations: RestOperations,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val clock: Clock // Inject a Clock for easier testing
) : CommonHttpClient {

    private val logger = KotlinLogging.logger {}

    private lateinit var circuitBreaker: CircuitBreaker
    private lateinit var retry: Retry
    private var nextAllowedRequestTime: Instant = Instant.MIN // Initialize with a time in the past

    //TODO extract these to be injected from configuration
    @PostConstruct
    fun init() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("faultTolerantHttpClientClientBreaker")

        val retryConfig = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .intervalFunction { attempt -> Duration.ofMillis(500 * attempt.toLong()).get(ChronoUnit.SECONDS) }
            .retryOnException { exception -> exception is JokeApiException }
            .build()

        retry = Retry.of("faultTolerantHttpClientRetry", retryConfig)
    }

    override fun <T> executeRequest(url: String, responseType: Class<T>): T {
        if (clock.instant().isBefore(nextAllowedRequestTime)) {
            throw JokeApiRateLimitException("Rate limit exceeded. Try again later.")
        }

        return Retry.decorateCheckedSupplier(retry, CircuitBreaker.decorateCheckedSupplier(circuitBreaker) {
            val response = restOperations.getForEntity(url, responseType)
            logUrlAndStatusCode(url, response)
            handleResponseStatus(url, response)

            response.body ?: throw JokeApiBadRequestException("API responded with empty body")
        }).apply()
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
        //For this exercise we will only handle the Retry-After header and ignore the X-RateLimit-Reset header
        //Also we will not handle the case when the Retry-After header is not a number
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