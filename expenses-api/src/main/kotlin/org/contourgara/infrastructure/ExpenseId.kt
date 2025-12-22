package org.contourgara.infrastructure

import org.jetbrains.exposed.v1.core.Table

object ExpenseId : Table("expense_id") {
    val expenseId = varchar("expense_id", 26)
}
