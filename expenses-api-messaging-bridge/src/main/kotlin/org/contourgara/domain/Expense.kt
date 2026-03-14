package org.contourgara.domain

data class Expense(
    private val expenseId: ExpenseId,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
)
