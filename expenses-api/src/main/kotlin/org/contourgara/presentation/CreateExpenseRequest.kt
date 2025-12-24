package org.contourgara.presentation

import kotlinx.serialization.Serializable
import org.contourgara.application.CreateExpenseParam
import ulid.ULID

@Serializable
data class CreateExpenseRequest(
    private val expenseId: ULID,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val memo: String,
) {
    fun toParam(): CreateExpenseParam =
        CreateExpenseParam(
            expenseId = expenseId,
            amount = amount,
            payer = payer,
            category = category,
            memo = memo,
    )
}
