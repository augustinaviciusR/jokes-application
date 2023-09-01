package lt.homeassignment.jokesapplication.clients

import lt.homeassignment.jokesapplication.model.Joke
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import java.time.Clock
import java.time.LocalDateTime

@Component
class RetryableHttpClient(restOperations: RestOperations, clock: Clock) : CommonHttpClient {

    override fun <T> executeRequest(url: String, responseType: Class<T>): T {
        return responseType.cast(
            Joke(
                id = "Why did the chicken cross the road?",
                iconUrl = "To get to the other side",
                url = "https://example.com/joke/1",
                value = "some wort of joke",
                categories = listOf("pun"),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        )
    }
}
