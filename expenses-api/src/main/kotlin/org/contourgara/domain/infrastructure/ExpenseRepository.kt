package org.contourgara.domain.infrastructure

import org.contourgara.domain.Expense

interface ExpenseRepository {
    fun create(expense: Expense): Expense
}
