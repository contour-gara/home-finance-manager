package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.Expense
import org.koin.core.annotation.Single

@Single
class CreateExpenseUseCase(
    private val eventSendClient: EventSendClient,
) {
    fun execute(createExpenseParam: CreateExpenseParam): CreateExpenseDto =
        createExpenseParam.toModel()
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
    fun toModel(): Pair<Snowflake, Expense> =
        Pair(
            first = messageId,
            second = Expense(
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
                amount = expense.amount,
                category = expense.category,
                payer = expense.payer,
                year = expense.year,
                month = expense.month,
                memo = expense.memo,
            )
    }
}
