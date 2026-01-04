package org.contourgara.domain

import ulid.ULID

@JvmInline
value class ExpenseId(val value: ULID)

@JvmInline
value class ExpenseEventId(val value: ULID)

data class Expense(
    val expenseId: ExpenseId,
    val amount: Int,
    val category: String,
    val payer: String,
    val year: Int,
    val month: Int,
    val memo: String,
)
