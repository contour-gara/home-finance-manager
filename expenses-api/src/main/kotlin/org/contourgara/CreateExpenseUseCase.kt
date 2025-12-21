package org.contourgara

import org.contourgara.domain.Expense

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
) {
    fun execute(expense: Expense): Unit =
        expenseRepository.create(expense)
}
