package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single

@Single
class CreateExpenseUseCase(
    private val ulidGenerator: UlidGenerator,
    private val eventSendClient: EventSendClient,
) {
    fun execute(createExpenseParam: CreateExpenseParam): CreateExpenseDto =
        ExpenseId(value = ulidGenerator.nextUlid())
            .let { createExpenseParam.toModel(expenseId = it) }
            .also { (messageId, expense) ->
                eventSendClient.createExpense(messageId = messageId, expense = expense)
            }
            .let { (_, expense) ->
                CreateExpenseDto.from(expense = expense)
            }
}

data class CreateExpenseParam(
    val messageId: Snowflake,
    val amount: Int,
    val category: String,
    val payer: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    fun toModel(expenseId: ExpenseId): Pair<Snowflake, Expense> =
        Pair(
            first = messageId,
            second = Expense(
                expenseId = expenseId,
                amount = amount,
                category = category,
                payer = payer,
                year = year,
                month = month,
                memo = memo,
            )
        )
}

data class CreateExpenseDto(
    val expenseId: String,
    val amount: Int,
    val category: String,
    val payer: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    companion object {
        fun from(expense: Expense): CreateExpenseDto =
            CreateExpenseDto(
                expenseId = expense.expenseId.value.toString(),
                amount = expense.amount,
                category = expense.category,
                payer = expense.payer,
                year = expense.year,
                month = expense.month,
                memo = expense.memo,
            )
    }
}
