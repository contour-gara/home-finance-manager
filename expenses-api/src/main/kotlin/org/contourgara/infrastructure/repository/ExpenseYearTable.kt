package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpenseYearTable : Table("expense_year") {
    val expenseId = varchar("expense_id", 26)
    val year = integer("year")
}
