package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEvent

interface ExpenseEventRepository {
    fun save(expenseEvent: ExpenseEvent): ExpenseEvent
}
