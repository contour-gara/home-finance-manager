package org.contourgara.application

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single

@Single
class CreateExpenseUseCase(
    private val ulidGenerator: UlidGenerator,
    private val expenseClient: ExpenseClient,
) {
    fun execute(createExpenseParam: CreateExpenseParam): CreateExpenseDto =
        ExpenseId(value = ulidGenerator.nextUlid())
            .let { createExpenseParam.toModel(expenseId = it) }
            .let { expenseClient.create(it) }
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
