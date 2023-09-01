package lt.homeassignment.jokesapplication.service

import lt.homeassignment.jokesapplication.clients.JokeProvider
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeApiException
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

@Service
class CacheableJokeService(
    private val jokeProvider: JokeProvider
) : JokeService {

    private val logger = KotlinLogging.logger {}

    private val jokesCache: ConcurrentHashMap<String, MutableSet<Joke>> = ConcurrentHashMap()
    private val cachedCategories: AtomicReference<Set<String>> = AtomicReference(setOf())

    init {
        try {
            logger.info("Initializing CacheableJokeService.")
            refreshCachedCategories()
            prefillJokeCache()
            // Ideally handle well-defined exception, in order to save time I am handling REST based exceptions
            // But I would prove not a good design if JokeProvider protocol would change
        } catch (e: RestClientResponseException) {
            // TODO handle with MDC for better logging
            logger.error("Failed to initialize caches for CacheableJokeService", e)
        }
    }

    override fun listAvailableCategories(): Set<String> {
        return cachedCategories.get().takeIf { it.isNotEmpty() } ?: run {
            logger.warn("Cached categories are empty. Refreshing.")
            refreshCachedCategories()
            cachedCategories.get()
        }
    }

    override fun getJoke(category: String?): Joke {
        val normalizedCategory = normalizeCategory(category)
        return try {
            logger.debug("Failed to fetch joke for category: $normalizedCategory")
            getRandomCachedJoke(normalizedCategory)
        } catch (e: JokeApiException) {
            logger.error("Fetching joke for category: $normalizedCategory", e)
            fetchAndCacheJoke(normalizedCategory)
        }
    }

    private fun normalizeCategory(category: String?) = category?.lowercase()
        ?: listAvailableCategories().random().lowercase()

    private fun refreshCachedCategories() {
        logger.debug("Refreshing cached joke categories.")
        cachedCategories.set(jokeProvider.listJokeCategories())
    }

    private fun prefillJokeCache() {
        // Prefill cache with one joke from each category to warm up
        cachedCategories.get().forEach { category ->
            try {
                fetchAndCacheJoke(category)
            } catch (e: RestClientResponseException) {
                logger.error("Failed to prefill cache for category: $category", e)
            }
        }
    }

    private fun fetchAndCacheJoke(category: String): Joke {
        val joke = jokeProvider.getRandomJoke(category)
        val categoryJokes = jokesCache.computeIfAbsent(category) { ConcurrentHashMap.newKeySet() }

        // Implement a simple cache eviction policy
        if (categoryJokes.size >= MAX_ALLOWED_CACHE_SIZE) {
            val evictedJoke = categoryJokes.first()
            categoryJokes.remove(evictedJoke)
        }

        categoryJokes.add(joke)
        logger.debug("Fetched and cached joke for category: $category")
        return joke
    }

    private fun getRandomCachedJoke(category: String): Joke {
        return jokesCache[category]?.random()
            ?: throw JokeApiException("No cached jokes available for category: $category")
    }

    // Avoiding the use of the cache for this method, query string is a free text field
    // Thus it would take too much effort to implement cache which would provide any value
    override fun searchForJokes(query: String): JokeSearchResult {
        logger.debug("Searching for jokes with query: $query")
        return jokeProvider.searchForJokes(query.lowercase())
    }

    fun getJokesCache(): ConcurrentHashMap<String, MutableSet<Joke>> {
        return jokesCache
    }
    fun getCachedCategories(): AtomicReference<Set<String>> {
        return cachedCategories
    }

    companion object {
        // Externalize the max cache size to a configuration property
        const val MAX_ALLOWED_CACHE_SIZE: Int = 100
    }
}
