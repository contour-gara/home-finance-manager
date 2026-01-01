package org.contourgara.domain.infrastructure

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId

interface ExpenseRepository {
    fun create(expense: Expense): Expense
    fun findById(expenseId: ExpenseId): Expense?
}
