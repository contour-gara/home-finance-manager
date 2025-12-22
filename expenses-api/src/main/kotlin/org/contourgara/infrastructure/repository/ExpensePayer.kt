package org.contourgara.infrastructure.repository

import org.jetbrains.exposed.v1.core.Table

object ExpensePayer : Table("expense_payer") {
    val expenseId = varchar("expense_id", 26)
    val payer = varchar("payer", 100)
}
