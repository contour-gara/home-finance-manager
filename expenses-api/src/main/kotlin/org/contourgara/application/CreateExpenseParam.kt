package org.contourgara.application

import arrow.core.EitherNel
import org.contourgara.domain.Error
import org.contourgara.domain.Expense

data class CreateExpenseParam(
    private val expenseId: String,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
) {
    fun toModel(): EitherNel<Error, Expense> =
        Expense.of(
            expenseId = expenseId,
            amount = amount,
            payer = payer,
            category = category,
            year = year,
            month = month,
            memo = memo,
        )
}
