package org.contourgara.domain

sealed interface Error {
    val title: String
    val detail: String
}

data class ValidationError(
    override val title: String = "Validation Error",
    val pointer: String,
    val invalidValue: String,
    override val detail: String,
) : Error {
    constructor(pointer: String, invalidValue: Int, detail: String) : this(pointer = pointer, invalidValue = invalidValue.toString(), detail = detail)
}

// TODO: ExpenseDuplicateError
// TODO: ExpenseNotFoundError
