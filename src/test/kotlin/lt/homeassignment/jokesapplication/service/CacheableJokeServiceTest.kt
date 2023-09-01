package lt.homeassignment.jokesapplication.service


import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import lt.homeassignment.jokesapplication.clients.JokeProvider
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertFailsWith


internal class CacheableJokeServiceTest {

    private val jokeProvider: JokeProvider = mockk()
    private lateinit var cacheableJokeService:CacheableJokeService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { jokeProvider.listJokeCategories() } returns setOf("tech", "science")
        cacheableJokeService = CacheableJokeService(jokeProvider)
    }

    @Test
    fun `test listAvailableCategories`() {
        val categories = setOf("tech", "science")
        every { jokeProvider.listJokeCategories() } returns categories

        val result = cacheableJokeService.listAvailableCategories()

        assertEquals(categories, result)
    }

    @Test
    fun `test getJoke with category`() {
        val joke = Joke("id1", "icon_url", LocalDateTime.now(), LocalDateTime.now(), "url", "A science joke", listOf("science"))
        every { jokeProvider.getRandomJoke("science") } returns joke

        val result = cacheableJokeService.getJoke("science")

        assertEquals(joke, result)
    }

    @Test
    fun `test getJoke without category`() {
        val categories = setOf("tech", "science")
        val joke = Joke("id1", "icon_url", LocalDateTime.now(), LocalDateTime.now(), "url", "A random joke", listOf("science"))
        every { jokeProvider.listJokeCategories() } returns categories
        every { jokeProvider.getRandomJoke(any()) } returns joke

        val result = cacheableJokeService.getJoke(null)

        assertEquals(joke, result)
    }

    @Test
    fun `test getJoke with fallback to cache`() {
        val joke = Joke("id1", "icon_url", LocalDateTime.now(), LocalDateTime.now(), "url", "A cached joke", listOf("science"))
        val categoryJokes = ConcurrentHashMap.newKeySet<Joke>()
        categoryJokes.add(joke)
        cacheableJokeService.getJokesCache()["science"] = categoryJokes
        every { jokeProvider.getRandomJoke("science") } throws Exception("Failed to fetch joke")

        val result = cacheableJokeService.getJoke("science")

        assertEquals(joke, result)
    }

    @Test
    fun `test getJoke with exception and no cached jokes`() {
        every { jokeProvider.getRandomJoke("science") } throws Exception("Failed to fetch joke")

        assertFailsWith<Exception> {
            cacheableJokeService.getJoke("science")
        }
    }

    @Test
    fun `test searchForJokes`() {
        val jokes = listOf(
            Joke("id1", "icon_url", LocalDateTime.now(), LocalDateTime.now(), "url", "A science joke", listOf("science")),
            Joke("id2", "icon_url", LocalDateTime.now(), LocalDateTime.now(), "url", "A tech joke", listOf("tech"))
        )
        val jokeSearchResult = JokeSearchResult(2, jokes)
        every { jokeProvider.searchForJokes("science") } returns jokeSearchResult

        val result = cacheableJokeService.searchForJokes("science")

        assertEquals(jokeSearchResult, result)
    }

}
