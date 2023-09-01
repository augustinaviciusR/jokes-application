package lt.homeassignment.jokesapplication.configuration

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.web.client.RestTemplate
import java.time.Clock
import java.time.Duration

@Configuration
class ApplicationConfig {

    /**
     * CORS Filter Configuration
     */
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

    /**
     * RestTemplate Configuration
     */
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

    /**
     * Circuit Breaker Configuration
     */
    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        val circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .permittedNumberOfCallsInHalfOpenState(10)
            .slidingWindow(1, 1, CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
            .build()

        return CircuitBreakerRegistry.of(circuitBreakerConfig)
    }

    /**
     * Clock Configuration
     */
    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }
}
