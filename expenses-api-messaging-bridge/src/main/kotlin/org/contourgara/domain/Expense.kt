package org.contourgara.domain

import kotlinx.datetime.LocalDate

data class Expense(
    val amount: Int,
    val payer: String,
    val category: String,
    val date: LocalDate,
    val memo: String,
)
