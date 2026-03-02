package org.contourgara.application

data class CreateExpenseParam(
    private val messageId: String,
    private val expenseId: String,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
)
