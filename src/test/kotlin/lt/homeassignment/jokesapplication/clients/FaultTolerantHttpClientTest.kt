package lt.homeassignment.jokesapplication.clients

import io.mockk.every
import io.mockk.mockk
import lt.homeassignment.jokesapplication.model.JokeApiRateLimitException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations
import java.time.Clock
import java.time.Instant

class FaultTolerantHttpClientTest {

    private lateinit var restOperations: RestOperations
    private lateinit var retryHandler: RetryHandler // Your new interface
    private lateinit var mockClock: Clock
    private lateinit var faultTolerantHttpClient: FaultTolerantHttpClient

    private val testURL = "https://api.chucknorris.io/jokes"

    @BeforeEach
    fun setUp() {
        restOperations = mockk()
        retryHandler = mockk() // Mock the new interface
        mockClock = mockk()

        faultTolerantHttpClient = FaultTolerantHttpClient(restOperations, retryHandler, mockClock)
    }

    @Test
    fun `test executeRequest returns expected value`() {
        val responseType = String::class.java
        val expectedResponse = "This is a joke"

        every {
            restOperations.getForEntity(testURL, responseType)
        } returns ResponseEntity(expectedResponse, HttpStatus.OK)
        every { retryHandler.executeWithRetry<String>(any()) } answers { firstArg<() -> String>().invoke() }
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:00.00Z")

        val actualResponse = faultTolerantHttpClient.executeRequest(testURL, responseType)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `test executeRequest throws JokeApiRateLimitException for rate-limited request`() {
        val responseType = String::class.java

        every {
            restOperations.getForEntity(testURL, responseType)
        } returns ResponseEntity("Rate limit Test", HttpStatus.TOO_MANY_REQUESTS)
        every { retryHandler.executeWithRetry<String>(any()) } throws JokeApiRateLimitException("Rate limit reached")
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:00.00Z")

        assertThrows(JokeApiRateLimitException::class.java) {
            faultTolerantHttpClient.executeRequest(testURL, responseType)
        }
    }

    @Test
    fun `test executeRequest honors Retry-After header`() {
        val responseType = String::class.java
        val expectedResponse = "This is a joke"

        every { retryHandler.executeWithRetry<String>(any()) } answers { firstArg<() -> String>().invoke() }
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:00.00Z")

        // First request to set the nextAllowedRequestTime
        every { restOperations.getForEntity(testURL, responseType) } returns ResponseEntity(
            "Rate limit Test",
            HttpHeaders().apply {
                this.set("Retry-After", "5")
            },
            HttpStatus.TOO_MANY_REQUESTS
        )
        assertThrows(JokeApiRateLimitException::class.java) {
            faultTolerantHttpClient.executeRequest(testURL, responseType)
        }

        every { restOperations.getForEntity(testURL, responseType) } returns ResponseEntity(
            expectedResponse,
            HttpStatus.OK
        )
        every { mockClock.instant() } returns Instant.parse("2023-09-01T10:00:15.00Z")

        val actualResponse = faultTolerantHttpClient.executeRequest(testURL, responseType)
        assertEquals(expectedResponse, actualResponse)
    }
}
