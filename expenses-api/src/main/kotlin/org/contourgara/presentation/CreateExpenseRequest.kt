package org.contourgara.presentation

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.contourgara.application.CreateExpenseParam

@Serializable
data class CreateExpenseRequest(
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val date: LocalDate,
    private val memo: String,
) {
    fun toParam(): CreateExpenseParam =
        CreateExpenseParam(
            amount = amount,
            payer = payer,
            category = category,
            date = date,
            memo = memo,
    )
}
