package org.contourgara.domain

interface ExpenseRepository {
    fun create(expense: Expense): Unit
}
