package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseIdTable : Table("expense_id") {
    val expenseId = varchar("expense_id", 26)
}
