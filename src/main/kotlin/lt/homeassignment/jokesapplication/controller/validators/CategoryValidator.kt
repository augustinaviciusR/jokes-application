package lt.homeassignment.jokesapplication.controller.validators

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import lt.homeassignment.jokesapplication.service.JokeService

class CategoryValidator(private val jokeService: JokeService) : ConstraintValidator<ValidCategory, String?> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrEmpty()) {
            return true
        }

        val availableCategories = jokeService.listAvailableCategories()
        return value.lowercase().trim() in availableCategories
    }
}
