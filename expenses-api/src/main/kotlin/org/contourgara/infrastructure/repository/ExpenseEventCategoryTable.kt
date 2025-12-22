package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseEventCategoryTable : Table("expense_event_category") {
    val expenseEventId = varchar("expense_event_id", 26)
    val eventCategory = varchar("event_category", 100)
}
