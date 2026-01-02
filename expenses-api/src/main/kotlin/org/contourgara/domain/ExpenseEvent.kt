package org.contourgara.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

data class ExpenseEvent(
    val expenseEventId: ExpenseEventId,
    val expenseId: ExpenseId,
    val eventCategory: EventCategory,
) {
    fun delete(deleteEventId: ExpenseEventId): Either<Error, ExpenseEvent> =
        also {
            require(this.expenseEventId < deleteEventId) { "deleteEventId must be greater than expenseEventId: deleteEventId = ${deleteEventId.value}, expenseEventId = ${expenseEventId.value}" }
        }
            .let {
                either {
                    ensure(condition = eventCategory == EventCategory.CREATE) {
                        ExpenseAlreadyDeletedError(
                            expenseId = expenseId.value.toString(),
                        )
                    }
                    copy(
                        expenseEventId = deleteEventId,
                        eventCategory = EventCategory.DELETE,
                    )
                }
            }
}
