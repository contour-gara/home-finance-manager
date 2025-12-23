package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventID

interface ExpenseEventRepository {
    fun save(expenseEvent: ExpenseEvent): ExpenseEventID
}
