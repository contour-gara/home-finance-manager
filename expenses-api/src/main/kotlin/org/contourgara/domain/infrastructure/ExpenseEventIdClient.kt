package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEventId

interface ExpenseEventIdClient {
    fun nextExpensesEventId(): ExpenseEventId
}
