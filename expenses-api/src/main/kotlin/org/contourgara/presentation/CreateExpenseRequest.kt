package org.contourgara.presentation

import kotlinx.serialization.Serializable
import org.contourgara.application.CreateExpenseParam

@Serializable
data class CreateExpenseRequest(
    private val expenseId: String,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
) {
    fun toParam(): CreateExpenseParam =
        CreateExpenseParam(
            expenseId = expenseId,
            amount = amount,
            payer = payer,
            category = category,
            year = year,
            month = month,
            memo = memo,
    )
}
