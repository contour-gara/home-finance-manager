package org.contourgara.application

import arrow.core.EitherNel
import kotlinx.datetime.LocalDate
import org.contourgara.domain.Error
import org.contourgara.domain.Expense

data class CreateExpenseParam(
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val date: LocalDate,
    private val memo: String,
) {
    fun toModel(expenseId: String): EitherNel<Error, Expense> =
        Expense.of(
            expenseId = expenseId,
            amount = amount,
            payer = payer,
            category = category,
            date = date,
            memo = memo,
        )
}
