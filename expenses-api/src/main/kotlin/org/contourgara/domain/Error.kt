package org.contourgara.domain

sealed interface Error {
    val title: String
    val detail: String
    val error: String
}

data class ValidationError(
    override val title: String = "Validation Error",
    val pointer: String,
    val invalidValue: String,
    override val detail: String,
) : Error {
    constructor(pointer: String, invalidValue: Int, detail: String) : this(pointer = pointer, invalidValue = invalidValue.toString(), detail = detail)

    override val error: String get() = "$detail: $pointer = $invalidValue"
}

fun List<Error>.toException(): ApplicationException =
    when (first()) {
        is ValidationError -> ValidationException(
            title = first().title,
            errors = map { it.error }
        )
    }

// TODO: ExpenseDuplicateError
// TODO: ExpenseNotFoundError

sealed class ApplicationException(
    open val title: String,
    open val errors: List<String>
) : RuntimeException("$title: $errors")

class ValidationException(
    override val title: String,
    override val errors: List<String>,
) : ApplicationException(title = title, errors = errors)
