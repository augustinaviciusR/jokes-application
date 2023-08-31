package lt.homeassignment.jokesapplication.controller.validators

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [CategoryValidator::class])
annotation class ValidCategory(
    val message: String = "Invalid category provided",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)