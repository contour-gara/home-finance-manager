package org.contourgara.domain

interface ExpenseEventRepository {
    fun save(expenseEvent: ExpenseEvent): Unit
}
