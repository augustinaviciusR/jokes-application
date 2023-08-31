package lt.homeassignment.jokesapplication.service

import lt.homeassignment.jokesapplication.service.JokeService
import lt.homeassignment.jokesapplication.clients.JokeProvider
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CacheableJokeService(
    private val jokeProvider: JokeProvider
) : JokeService {

    override fun listAvailableCategories(): List<String> {
        return listOf()
    }

    override fun getJoke(category: String?): Joke {
        return Joke(
            id = "Why did the chicken cross the road?",
            iconUrl = "To get to the other side",
            url = "https://example.com/joke/1",
            value = "some wort of joke",
            categories = listOf("pun"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }

    override fun searchForJokes(query: String): JokeSearchResult {
        return JokeSearchResult(
            total = 1,
            result = listOf(
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
        )
    }
}
