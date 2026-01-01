package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseId

interface ExpenseEventRepository {
    fun save(expenseEvent: ExpenseEvent): ExpenseEvent
    fun findByExpenseId(expenseId: ExpenseId): ExpenseEvent?
}
