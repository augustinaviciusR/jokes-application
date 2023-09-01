package lt.homeassignment.jokesapplication.clients

import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ChuckNorrisJokeClient(
    @Value("\${joke.api.url}") private val url: String,
    private val commonHttpClient: CommonHttpClient): JokeProvider {

    override fun listJokeCategories(): Set<String> {
        return commonHttpClient.executeRequest("$url/categories", Array<String>::class.java).toSortedSet()
    }

    override fun getRandomJoke(category: String?): Joke {
        val formedURL = if (category.isNullOrEmpty()) "$url/random" else "$url/random?category=$category"
        return commonHttpClient.executeRequest(formedURL, Joke::class.java)
    }

    override fun searchForJokes(query: String): JokeSearchResult {
        return commonHttpClient.executeRequest("$url/search?&query=$query", JokeSearchResult::class.java)
    }
}