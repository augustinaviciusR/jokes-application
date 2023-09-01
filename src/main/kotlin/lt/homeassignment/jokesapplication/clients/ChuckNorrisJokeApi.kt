package lt.homeassignment.jokesapplication.clients

import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChuckNorrisJokeApi () : JokeProvider {

    override fun listJokeCategories(): Set<String> {
        return setOf()
    }

    override fun getRandomJoke(category: String?): Joke {
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