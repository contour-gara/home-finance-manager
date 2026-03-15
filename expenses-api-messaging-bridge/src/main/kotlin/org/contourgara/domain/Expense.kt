package org.contourgara.domain

data class Expense(
    val expenseId: ExpenseId,
    val amount: Int,
    val payer: String,
    val category: String,
    val year: Int,
    val month: Int,
    val memo: String,
)
