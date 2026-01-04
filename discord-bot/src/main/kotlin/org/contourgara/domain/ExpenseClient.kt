package org.contourgara.domain

interface ExpenseClient {
    fun create(expense: Expense): Pair<Expense, ExpenseEventId>
}
