package lt.homeassignment.jokesapplication.controller

import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import lt.homeassignment.jokesapplication.service.JokeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/v1/api/jokes")
class JokesController(private val jokeService: JokeService) {
    @GetMapping("/search")
    fun searchJokes(@RequestParam(required = true) query: String): ResponseEntity<JokeSearchResult> {
        return ResponseEntity.ok(JokeSearchResult(
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
        ))
    }

    @GetMapping("/random")
    fun getJoke(@RequestParam(required = false) category: String?): ResponseEntity<Joke> {
        return ResponseEntity.ok( Joke(
            id = "Why did the chicken cross the road?",
            iconUrl = "To get to the other side",
            url = "https://example.com/joke/1",
            value = "some wort of joke",
            categories = listOf("pun"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        ))
    }
}