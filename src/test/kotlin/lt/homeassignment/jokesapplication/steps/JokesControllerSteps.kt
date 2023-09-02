package lt.homeassignment.jokesapplication.steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import lt.homeassignment.jokesapplication.SpringContextConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class JokesControllerSteps : SpringContextConfiguration() {

    private lateinit var response: ResponseEntity<Any>
    private lateinit var query: String
    private lateinit var category: String

    @Given("I have a valid query {string}")
    fun i_have_a_valid_query(query: String) {
        this.query = query
    }

    @Given("I have an invalid query {string}")
    fun i_have_a_invalid_query(query: String) {
        this.query = query
    }

    @Given("I have a query that exceeds 100 characters")
    fun i_have_a_query_that_exceeds_100_characters() {
        this.query = "A".repeat(101)
    }

    @Given("I have a valid category {string}")
    fun i_have_a_valid_category(category: String) {
        this.category = category
    }

    @Given("I have an invalid category {string}")
    fun i_have_a_invalid_category(category: String) {
        this.category = category
    }

    @When("I search for jokes with this query")
    fun i_search_for_jokes_with_this_query() {
        response = restTemplate.getForEntity("/v1/api/jokes/search?query=$query", Any::class.java)
    }

    @When("I search for jokes with invalid query")
    fun i_search_for_jokes_with_invalid_query() {
        response = restTemplate.getForEntity("/v1/api/jokes/search?query=$query", Any::class.java)
    }

    @When("I request a joke without category")
    fun i_request_a_joke_without_category() {
        response = restTemplate.getForEntity("/v1/api/jokes", Any::class.java)
    }

    @When("I request a joke with valid category")
    fun i_request_a_joke_with_valid_category() {
        response = restTemplate.getForEntity("/v1/api/jokes?category=$category", Any::class.java)
    }

    @When("I request a joke with this category")
    fun i_request_a_joke_with_this_category() {
        response = restTemplate.getForEntity("/v1/api/jokes?category=$category", Any::class.java)
    }

    @When("I request a joke with invalid category")
    fun i_request_a_joke_with_invalid_category() {
        response = restTemplate.getForEntity("/v1/api/jokes?category=$category", Any::class.java)
    }

    @Then("I should get a list of jokes")
    fun i_should_get_a_list_of_jokes() {
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Then("I should get an error message")
    fun i_should_get_an_error_message() {
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Then("I should get a random joke")
    fun i_should_get_a_random_joke() {
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Then("I should get a joke from the {string} category")
    fun i_should_get_a_joke_from_the_category(category: String) {
        this.category = category
    }

    @Then("I should get an error message for the invalid category")
    fun i_should_get_an_error_message_for_the_invalid_category() {
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}
