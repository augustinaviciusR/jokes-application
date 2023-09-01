package lt.homeassignment.jokesapplication.service

import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult

interface JokeService {
    fun listAvailableCategories(): Set<String>
    fun getJoke(category: String?): Joke
    fun searchForJokes(query: String): JokeSearchResult
}
