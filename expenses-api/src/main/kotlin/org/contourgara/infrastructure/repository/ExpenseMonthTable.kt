package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseMonthTable : Table("expense_month") {
    val expenseId = varchar("expense_id", 26)
    val month = integer("month")
}
