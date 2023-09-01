package lt.homeassignment.jokesapplication.clients

import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
interface JokeProvider {
    fun listJokeCategories(): Set<String>
    fun getRandomJoke(category: String?): Joke
    fun searchForJokes(query: String): JokeSearchResult
}
