package lt.homeassignment.jokesapplication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JokesApplication

fun main(args: Array<String>) {
    // ktlint caches this line as a warning, but it is required for Spring Boot to run
    @Suppress("SpreadOperator")
    runApplication<JokesApplication>(*args)
}
