package lt.homeassignment.jokesapplication.controller.validators

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import lt.homeassignment.jokesapplication.service.JokeService
import org.springframework.beans.factory.annotation.Autowired

class CategoryValidator : ConstraintValidator<ValidCategory, String?> {

    @Autowired
    private lateinit var jokeService: JokeService

    override fun initialize(constraintAnnotation: ValidCategory) {
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrEmpty()) {
            return true
        }

        val availableCategories = jokeService.listAvailableCategories()
        return value.lowercase().trim() in availableCategories
    }
}