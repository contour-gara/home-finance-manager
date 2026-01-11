package org.contourgara.domain

interface ExpenseClient {
    fun create(expense: Expense): Pair<Expense, ExpenseEventId>
    fun delete(expenseId: ExpenseId): Pair<ExpenseId, ExpenseEventId>
}
