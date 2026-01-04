package org.contourgara.application

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.koin.core.annotation.Single
import ulid.ULID

@Single
class CreateExpenseUseCase() {
    fun execute(createExpenseParam: CreateExpenseParam): CreateExpenseDto =
        ULID
            .nextULID()
            .let { ExpenseId(value = it) }
            .let { createExpenseParam.toModel(expenseId = it) }
            .let { Pair(first = it, second = ExpenseEventId(value = ULID.nextULID())) }
            .let { (expense, expenseEventId) ->
                CreateExpenseDto.from(expense = expense, expenseEventId = expenseEventId)
            }
}

data class CreateExpenseParam(
    val amount: Int,
    val category: String,
    val payer: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    fun toModel(expenseId: ExpenseId): Expense =
        Expense(
            expenseId = expenseId,
            amount = amount,
            category = category,
            payer = payer,
            year = year,
            month = month,
            memo = memo,
        )
}

data class CreateExpenseDto(
    val expenseId: String,
    val expenseEventId: String,
    val amount: Int,
    val category: String,
    val payer: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    companion object {
        fun from(expense: Expense, expenseEventId: ExpenseEventId): CreateExpenseDto =
            CreateExpenseDto(
                expenseId = expense.expenseId.value.toString(),
                expenseEventId = expenseEventId.value.toString(),
                amount = expense.amount,
                category = expense.category,
                payer = expense.payer,
                year = expense.year,
                month = expense.month,
                memo = expense.memo,
            )
    }
}
