package lt.homeassignment.jokesapplication.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import lt.homeassignment.jokesapplication.clients.JokeProvider
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class CacheableJokeServiceTest {

    private val jokeProvider: JokeProvider = mockk()
    private lateinit var cacheableJokeService: CacheableJokeService

    private val scienceJoke = Joke(
        id = "id1",
        iconUrl = "icon_url",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        url = "url",
        value = "A science joke",
        categories = listOf("science")
    )

    private val techJoke = Joke(
        id = "id1",
        iconUrl = "icon_url",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        url = "url",
        value = "A tech joke",
        categories = listOf("tech")
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { jokeProvider.listJokeCategories() } returns setOf("tech", "science")
        every { jokeProvider.getRandomJoke("tech") } returns techJoke
        every { jokeProvider.getRandomJoke("science") } returns scienceJoke
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
    fun `test listAvailableCategories prefil caches if empty`() {
        cacheableJokeService.getCachedCategories().set(setOf())
        val categories = setOf("tech", "science")
        every { jokeProvider.listJokeCategories() } returns categories

        val result = cacheableJokeService.listAvailableCategories()

        assertEquals(categories, result)
    }

    @Test
    fun `test getJoke with category`() {
        every { jokeProvider.getRandomJoke("science") } returns scienceJoke

        val result = cacheableJokeService.getJoke("science")

        assertEquals(scienceJoke, result)
    }

    @Test
    fun `test getJoke without category`() {
        val categories = setOf("tech", "science")
        every { jokeProvider.listJokeCategories() } returns categories
        every { jokeProvider.getRandomJoke(any()) } returns scienceJoke

        val result = cacheableJokeService.getJoke(null)

        assertNotNull(result)
    }

    @Test
    fun `test getJoke with fallback to cache`() {
        val categoryJokes = ConcurrentHashMap.newKeySet<Joke>()
        categoryJokes.add(scienceJoke)
        cacheableJokeService.getJokesCache()["science"] = categoryJokes
        every { jokeProvider.getRandomJoke("science") } throws Exception("Failed to fetch joke")

        val result = cacheableJokeService.getJoke("science")

        assertEquals(scienceJoke, result)
    }

    @Test
    fun `test getJoke with exception and no cached jokes`() {
        cacheableJokeService.getJokesCache().clear()
        every { jokeProvider.getRandomJoke("science") } throws Exception("Failed to fetch joke")

        assertFailsWith<Exception> {
            cacheableJokeService.getJoke("science")
        }
    }

    @Test
    fun `test searchForJokes`() {
        val jokes = listOf(
            scienceJoke,
            techJoke
        )
        val jokeSearchResult = JokeSearchResult(2, jokes)
        every { jokeProvider.searchForJokes("science") } returns jokeSearchResult

        val result = cacheableJokeService.searchForJokes("science")

        assertEquals(jokeSearchResult, result)
    }
}
