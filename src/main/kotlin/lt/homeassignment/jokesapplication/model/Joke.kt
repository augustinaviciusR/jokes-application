package lt.homeassignment.jokesapplication.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class JokeSearchResult(
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("result")
    val result: List<Joke>
)

data class Joke(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("icon_url")
    val iconUrl: String,
    @JsonProperty("created_at")
    //Done to avoid "Cannot deserialize value of type `java.time.LocalDateTime` from String" error
    // However this is not the best solution, because it will not work if the date format changes
    // A better solution would be to create a custom deserializer
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private val createdAt: LocalDateTime,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    val updatedAt: LocalDateTime,
    @JsonProperty("url")
    val url: String,
    @JsonProperty("value")
    val value: String,
    @JsonProperty("categories")
    val categories: List<String>
)
