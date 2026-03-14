package org.contourgara.infrastructure

import org.jetbrains.exposed.v1.core.Table

object ExpenseIdTable : Table("expense_id") {
    val expenseIdColumn = varchar("expense_id", 26)
    val messageIdColumn = varchar("message_id", 19)
}
