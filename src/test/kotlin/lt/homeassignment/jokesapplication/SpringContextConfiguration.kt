package lt.homeassignment.jokesapplication

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container

@TestConfiguration
class WireMockConfig {
    companion object {
        @Container
        val wireMockContainer = GenericContainer<Nothing>("wiremock/wiremock:2.30.1").apply {
            withExposedPorts(8080)
            withClasspathResourceMapping("wiremock-stubs/", "home/wiremock/mappings/", BindMode.READ_ONLY)
            waitingFor(Wait.forHttp("/jokes/categories"))
        }.also { it.start() }
    }

    @Bean
    fun wireMockContainer() = wireMockContainer
}

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@CucumberContextConfiguration
@ContextConfiguration(
    classes = [JokesApplication::class, WireMockConfig::class],
    initializers = [SpringContextConfiguration.Initializer::class]
)
abstract class SpringContextConfiguration {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            val wireMockContainer = WireMockConfig.wireMockContainer
            val mappedPort = wireMockContainer.getMappedPort(8080)
            TestPropertyValues.of("wiremock.server.port=$mappedPort").applyTo(applicationContext.environment)
            TestPropertyValues.of("joke.api.url=http://localhost:$mappedPort/jokes")
                .applyTo(applicationContext.environment)
        }
    }
}
