package org.contourgara

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseRepository

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
) {
    fun execute(expense: Expense): Unit =
        expenseRepository.create(expense)
}
