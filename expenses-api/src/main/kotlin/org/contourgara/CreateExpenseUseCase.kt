package org.contourgara

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
) {
    fun execute(expense: Expense): Unit =
        transaction { expenseRepository.create(expense) }
}
