package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseEventIdTable : Table("expense_event_id") {
    val expenseEventId = varchar("expense_event_id", 26)
}
