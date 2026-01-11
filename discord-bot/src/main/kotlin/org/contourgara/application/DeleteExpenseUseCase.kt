package org.contourgara.application

import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseId
import org.koin.core.annotation.Single
import ulid.ULID

@Single
class DeleteExpenseUseCase(
    private val expenseClient: ExpenseClient,
) {
    fun execute(
        expenseId: String,
    ): Pair<String, String> =
        ExpenseId(value = ULID.parseULID(ulidString = expenseId))
            .let { expenseClient.delete(expenseId = it) }
            .let { (expenseId, expenseEventId) ->
                Pair(
                    first = expenseId.value.toString(),
                    second = expenseEventId.value.toString(),
                )
            }
}
