package lt.homeassignment.jokesapplication.clients

import io.mockk.*
import io.mockk.impl.annotations.MockK
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

class RetryableHttpClientTest {

    private lateinit var retryableHttpClient: RetryableHttpClient

    @MockK
    private lateinit var restOperations: RestOperations

    @MockK
    private lateinit var clock: Clock

    private val testURL = "https://api.chucknorris.io/jokes"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        retryableHttpClient = RetryableHttpClient(restOperations, clock)
    }

    @Test
    fun `test ExecuteRequest Success`() {
        val responseType = String::class.java
        val expectedResponse = "Success Response"
        val responseEntity = ResponseEntity(expectedResponse, HttpStatus.OK)

        every { restOperations.getForEntity(testURL, responseType) } returns responseEntity

        val result = retryableHttpClient.executeRequest(testURL, responseType)

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `test executeRequest RateLimitExceeded`() {
        val responseType = String::class.java
        val retryAfterHeader = "5"
        val responseHeaders = HttpHeaders()
        responseHeaders.add("Retry-After", retryAfterHeader)
        val responseEntity = ResponseEntity("Rate Limit Exceeded", responseHeaders, HttpStatus.TOO_MANY_REQUESTS)

        every { restOperations.getForEntity(testURL, responseType) } returns responseEntity
        every { clock.instant() } returns Instant.parse("2023-09-01T00:00:00Z")

        val ex = assertThrows(JokeApiRateLimitException::class.java) {
            retryableHttpClient.executeRequest(testURL, responseType)
        }

        assertEquals("API rate limit exceeded. Retry after $retryAfterHeader seconds.", ex.message)
    }

}