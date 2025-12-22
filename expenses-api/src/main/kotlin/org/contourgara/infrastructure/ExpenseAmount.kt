package org.contourgara.infrastructure

import org.jetbrains.exposed.v1.core.Table

object ExpenseAmount : Table("expense_amount") {
    val expenseId = varchar("expense_id", 26)
    val amount = integer("amount")
}
