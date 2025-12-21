package org.contourgara.domain

import ulid.ULID

data class Expense(
    val id: ULID,
    val amount: Int,
    val payer: Payer,
    val category: Category,
    val memo: String,
)
