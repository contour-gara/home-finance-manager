package org.contourgara.application

import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import ulid.ULID

data class CreateExpenseDto(
    val expenseId: ULID,
    val expenseEventId: ULID,
) {
    companion object {
        fun of(expenseId: ExpenseId, expenseEventId: ExpenseEventId): CreateExpenseDto =
            CreateExpenseDto(
                expenseId = expenseId.value,
                expenseEventId = expenseEventId.value,
            )
    }
}
