package lt.homeassignment.jokesapplication.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import com.ninjasquad.springmockk.MockkBean
import lt.homeassignment.jokesapplication.model.Joke
import lt.homeassignment.jokesapplication.model.JokeSearchResult
import lt.homeassignment.jokesapplication.service.JokeService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime
import kotlin.test.Test

@WebMvcTest(JokesController::class)
class JokesControllerTest(@Autowired val mockMvc: MockMvc){

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var jokeService: JokeService

    private val testJoke = Joke(id="1",
        iconUrl = "icon1",
        createdAt = LocalDateTime.parse("2023-08-31T12:00:00"),
        updatedAt =  LocalDateTime.parse("2023-08-31T12:00:00"),
        url = "url1",
        value = "joke 1",
        emptyList())

    @Test
    fun `searchJokes should return jokes when query is provided`() {
        val query = "funny"
        val jokes = listOf(testJoke)
        val result = JokeSearchResult(jokes.size, jokes)
        every { jokeService.searchForJokes(query) } returns result

        mockMvc.get("/v1/api/jokes/search") {
            param("query", query)
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(result)) }
        }
    }

    @Test
    fun `searchJokes should return 400 Bad Request when query is too long`() {
        val query = "a".repeat(101)
        mockMvc.get("/v1/api/jokes/search") {
            param("query", query)
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `getJoke should return joke when category is null or empty`() {
        every { jokeService.listAvailableCategories() } returns setOf("animals", "funny")
        every { jokeService.getJoke(null) } returns testJoke

        mockMvc.get("/v1/api/jokes") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(testJoke)) }
        }

        mockMvc.get("/v1/api/jokes") {
            param("category", "")
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(testJoke)) }
        }
    }
    @Test
    fun `getJoke should return joke when valid category provided`() {
        every { jokeService.listAvailableCategories() } returns setOf("animals", "funny")
        every { jokeService.getJoke("animals") } returns testJoke

        mockMvc.get("/v1/api/jokes") {
            param("category", "animals")
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { json(objectMapper.writeValueAsString(testJoke)) }
        }
    }

    @Test
    fun `getJoke should return 400 Bad Request when category is too short`() {
        every { jokeService.listAvailableCategories() } returns setOf("animals", "funny")
        mockMvc.get("/v1/api/jokes") {
            param("category", "ab")
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `getJoke should return 400 Bad Request when category is too long`() {
        every { jokeService.listAvailableCategories() } returns setOf("animals", "funny")
        val longCategory = "a".repeat(101)
        mockMvc.get("/v1/api/jokes") {
            param("category", longCategory)
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}