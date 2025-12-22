package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseMemo : Table("expense_memo") {
    val expenseId = varchar("expense_id", 26)
    val memo = varchar("memo", 100)
}
