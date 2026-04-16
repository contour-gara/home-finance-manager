package org.contourgara.domain

import kotlinx.datetime.LocalDate
import ulid.ULID

@JvmInline
value class ExpenseId(val value: ULID)

@JvmInline
value class ExpenseEventId(val value: ULID)

data class Expense(
    val amount: Int,
    val category: String,
    val payer: String,
    val localDate: LocalDate,
    val memo: String,
)
