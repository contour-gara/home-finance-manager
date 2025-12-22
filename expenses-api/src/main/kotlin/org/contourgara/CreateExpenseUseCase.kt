package org.contourgara

import org.contourgara.domain.EventCategory
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventRepository
import org.contourgara.domain.ExpenseRepository
import org.contourgara.domain.UlidClient
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val ulidClient: UlidClient,
    private val expenseEventRepository: ExpenseEventRepository,
) {
    fun execute(expense: Expense): Unit =
        transaction {
            val expenseId = expenseRepository.create(expense)
            val eventId = ulidClient.nextUlid()
            expenseEventRepository.save(
                ExpenseEvent(
                    eventId = eventId,
                    expenseId = expenseId,
                    eventCategory = EventCategory.CREATED,
                )
            )
        }
}
