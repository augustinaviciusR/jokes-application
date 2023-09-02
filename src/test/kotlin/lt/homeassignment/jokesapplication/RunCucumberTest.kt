package lt.homeassignment.jokesapplication

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@SuppressWarnings("EmptyClassBlock")
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["classpath:features"],
    glue = ["lt.homeassignment.jokesapplication.steps"]
)
class RunCucumberTest
