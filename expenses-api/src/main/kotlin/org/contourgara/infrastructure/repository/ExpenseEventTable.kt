package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseEventTable : Table("expense_event") {
    val expenseEventId = varchar("expense_event_id", 26)
    val expenseId = varchar("expense_id", 26)
}
