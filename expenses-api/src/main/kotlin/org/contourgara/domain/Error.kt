package org.contourgara.domain

sealed interface Error {
    val title: String
}

data class ValidationError(
    override val title: String = "Validation Error",
    val pointer: String,
    val invalidValue: String,
    val detail: String,
) : Error

// TODO: ExpenseNotFoundError
