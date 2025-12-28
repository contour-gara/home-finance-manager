package org.contourgara.domain

data class Expenses(
    val lastEventId: ExpenseEventId,
    val year: Year,
    val month: Month,
    val payer: Payer,
    val category: Category,
    val amount: Int,
)
