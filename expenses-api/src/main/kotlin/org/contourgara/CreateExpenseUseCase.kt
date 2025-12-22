package org.contourgara

import org.contourgara.domain.EventCategory
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.UlidClient
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val ulidClient: UlidClient,
    private val expenseEventRepository: ExpenseEventRepository,
) {
    fun execute(expense: Expense): Unit =
        transaction {
            Pair(
                first = expenseRepository.create(expense),
                second = ulidClient.nextUlid(),
            )
                .let { (expenseId, expenseEventID) ->
                    expenseEventRepository.save(
                        ExpenseEvent(
                            expenseEventID = expenseEventID,
                            expenseId = expenseId,
                            eventCategory = EventCategory.CREATE,
                        )
                    )
                }
        }
}
