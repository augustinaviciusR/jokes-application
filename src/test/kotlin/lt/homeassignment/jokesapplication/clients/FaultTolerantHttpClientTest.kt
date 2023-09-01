package lt.homeassignment.jokesapplication.clients

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.mockk.every
import io.mockk.mockk
import lt.homeassignment.jokesapplication.model.JokeApiRateLimitException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.springframework.web.client.RestOperations

import org.springframework.http.*
import java.lang.Thread.sleep
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test

class FaultTolerantHttpClientTest {

    private lateinit var restOperations: RestOperations
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry
    private lateinit var mockClock: Clock
    private lateinit var faultTolerantHttpClient: FaultTolerantHttpClient

    private val testURL = "https://api.chucknorris.io/jokes"

    @BeforeEach
    fun setUp() {
        restOperations = mockk()
        circuitBreakerRegistry = mockk()
        mockClock = mockk()
        faultTolerantHttpClient = FaultTolerantHttpClient(restOperations, circuitBreakerRegistry, mockClock)

        val circuitBreaker: CircuitBreaker = mockk(relaxed = true)
        every { circuitBreakerRegistry.circuitBreaker("faultTolerantHttpClientClientBreaker") } returns circuitBreaker
        faultTolerantHttpClient.init()
    }

    @Test
    fun `test executeRequest returns expected value`() {
        val responseType = String::class.java
        val expectedResponse = "This is a joke"
        val responseEntity = ResponseEntity(expectedResponse, HttpStatus.OK)

        every { restOperations.getForEntity(testURL, responseType) } returns responseEntity
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:00.00Z")

        val actualResponse = faultTolerantHttpClient.executeRequest(testURL, responseType)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test executeRequest throws JokeApiRateLimitException for rate-limited request if Retry-After does not exist`() {
        val responseType = String::class.java
        val rateLimitedResponse = ResponseEntity<String>("Rate limit Test", HttpStatus.TOO_MANY_REQUESTS)

        every { restOperations.getForEntity(testURL, responseType) } returns rateLimitedResponse
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:00.00Z")

        assertThrows(JokeApiRateLimitException::class.java) {
            faultTolerantHttpClient.executeRequest(testURL, responseType)
        }
    }

    @Test
    fun `test executeRequest honors Retry-After header`() {
        val responseType = String::class.java
        val expectedResponse = "This is a joke"
        val rateLimitedHeaders = HttpHeaders()
        rateLimitedHeaders.set("Retry-After", "5")
        val rateLimitedResponse =
            ResponseEntity<String>("Rate limit Test", rateLimitedHeaders, HttpStatus.TOO_MANY_REQUESTS)

        every { restOperations.getForEntity(testURL, responseType) } returns rateLimitedResponse
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:00.00Z")

        // First request to set the nextAllowedRequestTime
        assertThrows(JokeApiRateLimitException::class.java) {
            faultTolerantHttpClient.executeRequest(testURL, responseType)
        }

        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:15.00Z")
        // Now change the response to be successful
        every { restOperations.getForEntity(testURL, responseType) } returns ResponseEntity(
            expectedResponse,
            HttpStatus.OK
        )

        // Third request should be successful
        val actualResponse = faultTolerantHttpClient.executeRequest(testURL, responseType)
        assertEquals(expectedResponse, actualResponse)
    }

}
