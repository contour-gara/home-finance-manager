package org.contourgara.domain

data class Expense(
    val expenseId: ExpenseId,
    val amount: Amount,
    val payer: Payer,
    val category: Category,
    val year: Year,
    val month: Month,
    val memo: String,
)
