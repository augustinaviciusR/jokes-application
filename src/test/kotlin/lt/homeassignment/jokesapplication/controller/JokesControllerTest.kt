package lt.homeassignment.jokesapplication.controller

import io.mockk.every
import io.mockk.mockk
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import lt.homeassignment.jokesapplication.service.JokeService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

class JokesControllerTest {

    private lateinit var jokeService: JokeService
    private lateinit var jokesController: JokesController

    private val testJoke = Joke(
        id = "1",
        iconUrl = "icon1",
        createdAt = LocalDateTime.parse("2023-08-31T12:00:00"),
        updatedAt = LocalDateTime.parse("2023-08-31T12:00:00"),
        url = "url1",
        value = "joke 1",
        emptyList()
    )

    @BeforeEach
    fun setup() {
        jokeService = mockk()
        jokesController = JokesController(jokeService)
    }

    @Test
    fun `test searchJokes returns jokes`() {
        val query = "test"
        val expectedResult = JokeSearchResult(
            total = 1,
            result = listOf(testJoke)
        )

        every { jokeService.searchForJokes(query.lowercase().trim()) } returns expectedResult

        val result = jokesController.searchJokes(query)

        Assertions.assertEquals(ResponseEntity.ok(expectedResult), result)
    }

    @Test
    fun `test getAJoke returns a joke for a given category`() {
        val category = "funny"

        every { jokeService.getJoke(category.lowercase().trim()) } returns testJoke

        val result = jokesController.getAJoke(category)

        Assertions.assertEquals(ResponseEntity.ok(testJoke), result)
    }

    @Test
    fun `test getAJoke returns a joke when no category is provided`() {
        val expectedResult = testJoke

        every { jokeService.getJoke(null) } returns expectedResult

        val result = jokesController.getAJoke(null)

        Assertions.assertEquals(ResponseEntity.ok(expectedResult), result)
    }
}
