package org.contourgara

import org.contourgara.domain.Expense

interface ExpenseRepository {
    fun create(expense: Expense): Unit
}
