package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate

data class Expense(
    val expenseId: ExpenseId,
    val amount: Amount,
    val payer: Payer,
    val category: Category,
    val year: Year,
    val month: Month,
    val memo: Memo,
) {
    companion object {
        fun of(
            expenseId: String,
            amount: Int,
            payer: String,
            category: String,
            year: Int,
            month: Int,
            memo: String,
        ): EitherNel<Error, Expense> =
            either {
                zipOrAccumulate(
                    action1 = { ExpenseId.of(value = expenseId).bindNel() },
                    action2 = { Amount.of(value = amount).bindNel() },
                    action3 = { Payer.of(value = payer).bindNel() },
                    action4 = { Category.of(value = category).bindNel() },
                    action5 = { Year.ofValidate(value = year).bindNel() },
                    action6 = { Month.ofValidate(value = month).bindNel() },
                    action7 = { Memo.of(value = memo).bindNel() },
                ) { expenseId, amount, payer, category, year, month, memo ->
                    Expense(
                        expenseId = expenseId,
                        amount = amount,
                        payer = payer,
                        category = category,
                        year = year,
                        month = month,
                        memo = memo,
                    )
                }
            }
    }
}
