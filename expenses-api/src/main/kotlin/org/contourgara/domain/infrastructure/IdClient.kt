package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId

interface IdClient {
    fun nextExpensesId(): ExpenseId
    fun nextExpensesEventId(): ExpenseEventId
}
