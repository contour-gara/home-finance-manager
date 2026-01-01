package org.contourgara.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

data class ExpenseEvent(
    val expenseEventId: ExpenseEventId,
    val expenseId: ExpenseId,
    val eventCategory: EventCategory,
) {
    fun delete(expenseEventId: ExpenseEventId): Either<Error, ExpenseEvent> =
        either {
            ensure(condition = eventCategory == EventCategory.CREATE) {
                ExpenseAlreadyDeletedError(
                    expenseId = expenseId.value.toString(),
                )
            }
            copy(
                expenseEventId = expenseEventId,
                eventCategory = EventCategory.DELETE,
            )
        }
}
