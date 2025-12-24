package org.contourgara.application

import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import ulid.ULID

data class CreateExpenseParam(
    private val expenseId: ULID,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
) {
    fun toModel(): Expense =
        Expense(
            expenseId = ExpenseId(expenseId),
            amount = amount,
            payer = Payer.valueOf(value = payer),
            category = Category.valueOf(value = category),
            year = Year.of(intYear = year),
            month = Month.of(intMonth = month),
            memo = memo,
        )
}
