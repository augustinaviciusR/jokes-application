package lt.homeassignment.jokesapplication.configuration

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import lt.homeassignment.jokesapplication.model.JokeApiException
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.time.Clock
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
class ApplicationConfig {

    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        // Please adjust the CORS settings according to your security requirements
        val config = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOrigin("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }

        return FilterRegistrationBean(CorsFilter(source)).apply {
            order = 0
        }
    }

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        val circuitBreakerConfig =
            CircuitBreakerConfig.custom().failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(Duration.ofSeconds(CIRCUIT_BREAKER_WAIT_OPEN_STATE_DURATION.toLong()))
                .permittedNumberOfCallsInHalfOpenState(CIRCUIT_BREAKER_FAILURE_PERMITTED_CALL_IN_HALF_OPEN_STATE)
                .slidingWindow(
                    CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE,
                    CIRCUIT_BREAKER_NUMBER_OF_CALLS,
                    CircuitBreakerConfig.SlidingWindowType.TIME_BASED
                ).build()

        return CircuitBreakerRegistry.of(circuitBreakerConfig)
    }

    @Bean
    fun faultTolerantHttpClientClientBreaker(circuitBreakerRegistry: CircuitBreakerRegistry): CircuitBreaker {
        return circuitBreakerRegistry.circuitBreaker("faultTolerantHttpClientClientBreaker")
    }

    @Bean
    fun faultTolerantHttpClientRetry(): Retry {
        val retryConfig = RetryConfig.custom<Any>().maxAttempts(MAX_RETRY_ATTEMPTS)
            .intervalFunction { attempt ->
                Duration.ofMillis(BASELINE_TIMEOUT_DURATION * attempt.toLong()).get(ChronoUnit.SECONDS)
            }.retryOnException { exception -> exception is JokeApiException }.build()

        return Retry.of("faultTolerantHttpClientRetry", retryConfig)
    }

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }

    companion object {
        const val CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD = 50.0f
        const val CIRCUIT_BREAKER_WAIT_OPEN_STATE_DURATION = 60
        const val CIRCUIT_BREAKER_FAILURE_PERMITTED_CALL_IN_HALF_OPEN_STATE = 10
        const val CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE = 5
        const val CIRCUIT_BREAKER_NUMBER_OF_CALLS = 5
        const val MAX_RETRY_ATTEMPTS = 3
        const val BASELINE_TIMEOUT_DURATION = 500
    }
}
