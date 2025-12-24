package org.contourgara.presentation

import kotlinx.serialization.Serializable
import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Payer
import ulid.ULID

@Serializable
data class CreateExpenseRequest(
    private val expenseId: ULID,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val memo: String,
) {
    fun toModel(): Expense = Expense(
        expenseId = ExpenseId(expenseId),
        amount = amount,
        payer = Payer.valueOf(payer),
        category = Category.valueOf(category),
        memo = memo,
    )
}
