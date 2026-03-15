package org.contourgara.domain

interface ExpenseClient {
    fun create(expense: Expense): Pair<ExpenseId, ExpenseEventId>
    fun delete(expenseId: ExpenseId)
}
