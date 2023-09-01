package lt.homeassignment.jokesapplication.clients

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import kotlin.test.Test

class ChuckNorrisJokeClientTest {

    private val testURL = "https://api.chucknorris.io/jokes"
    private val commonHttpClient: CommonHttpClient = mockk()
    private lateinit var chuckNorrisJokeClient: ChuckNorrisJokeClient

    private val categories = setOf("tech", "science")
    private val testJoke = Joke(
        id = "Why did the chicken cross the road?",
        iconUrl = "To get to the other side",
        url = "https://example.com/joke/1",
        value = "some wort of joke",
        categories = listOf("pun"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val testJokeSearchResult = JokeSearchResult(
        total = 1,
        result = listOf(
            Joke(
                id = "Why did the chicken cross the road?",
                iconUrl = "To get to the other side",
                url = "https://example.com/joke/1",
                value = "some wort of joke",
                categories = listOf("pun"),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        chuckNorrisJokeClient = ChuckNorrisJokeClient(testURL, commonHttpClient)
    }

    @Test
    fun `test listJokeCategories`() {
        val categories = listOf("tech", "science")
        every {
            commonHttpClient.executeRequest(
                "$testURL/categories",
                Array<String>::class.java
            )
        } returns categories.toTypedArray()

        val result = chuckNorrisJokeClient.listJokeCategories()

        assertEquals(categories.toSortedSet(), result)
    }

    @Test
    fun `test getRandomJoke without category`() {
        every { commonHttpClient.executeRequest("$testURL/random", Joke::class.java) } returns testJoke

        val result = chuckNorrisJokeClient.getRandomJoke(null)

        assertEquals(testJoke, result)
    }

    @Test
    fun `test getRandomJoke with category`() {
        val testCategory = categories.random()
        every {
            commonHttpClient.executeRequest(
                "$testURL/random?category=$testCategory",
                Joke::class.java
            )
        } returns testJoke

        val result = chuckNorrisJokeClient.getRandomJoke(testCategory)

        assertEquals(testJoke, result)
    }

    @Test
    fun `test searchForJokes`() {
        val query = "some random query"
        every {
            commonHttpClient.executeRequest(
                "$testURL/search?&query=$query",
                JokeSearchResult::class.java
            )
        } returns testJokeSearchResult

        val result = chuckNorrisJokeClient.searchForJokes(query)

        assertEquals(testJokeSearchResult, result)
    }
}
