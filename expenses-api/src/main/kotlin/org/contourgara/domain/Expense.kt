package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import kotlinx.datetime.LocalDate

data class Expense(
    val expenseId: ExpenseId,
    val amount: Amount,
    val payer: Payer,
    val category: Category,
    val date: ValidatedDate,
    val memo: Memo,
) {
    companion object {
        fun of(
            expenseId: String,
            amount: Int,
            payer: String,
            category: String,
            date: LocalDate,
            memo: String,
        ): EitherNel<Error, Expense> =
            either {
                zipOrAccumulate(
                    action1 = { ExpenseId.of(value = expenseId).bindNel() },
                    action2 = { Amount.of(value = amount).bindNel() },
                    action3 = { Payer.of(value = payer).bindNel() },
                    action4 = { Category.of(value = category).bindNel() },
                    action5 = { ValidatedDate.of(value = date).bindNel() },
                    action6 = { Memo.of(value = memo).bindNel() },
                ) { expenseId, amount, payer, category, date, memo ->
                    Expense(
                        expenseId = expenseId,
                        amount = amount,
                        payer = payer,
                        category = category,
                        date = date,
                        memo = memo,
                    )
                }
            }
    }
}
