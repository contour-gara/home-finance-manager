package org.contourgara.infrastructure

import org.jetbrains.exposed.v1.core.Table

object ExpenseCategory : Table("expense_category") {
    val expenseId = varchar("expense_id", 26)
    val category = varchar("category", 100)
}
