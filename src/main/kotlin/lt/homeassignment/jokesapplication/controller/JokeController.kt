package lt.homeassignment.jokesapplication.controller

import jakarta.validation.constraints.Size
import lt.homeassignment.jokesapplication.controller.validators.ValidCategory
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import lt.homeassignment.jokesapplication.service.JokeService
import org.slf4j.MDC
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/v1/api/jokes")
class JokesController(private val jokeService: JokeService) {
    @GetMapping("/search")
    fun searchJokes(@RequestParam(required = true) @Size(min = 3, max = 100) query: String): ResponseEntity<JokeSearchResult> {
        val formattedQuery = query.lowercase().trim()
        val jokes = jokeService.searchForJokes(formattedQuery)
        return ResponseEntity.ok(jokes)
    }

    @GetMapping()
    fun getJoke(@RequestParam(required = false) @ValidCategory category: String?): ResponseEntity<Joke> {
        if (category.isNullOrEmpty()) {
            val joke = jokeService.getJoke(null)
            return ResponseEntity.ok(joke)
        }
        val joke = jokeService.getJoke(category.lowercase().trim())
        return ResponseEntity.ok(joke)
    }
}