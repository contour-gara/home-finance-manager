package org.contourgara.application

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.Expense
import org.koin.core.annotation.Single

@Single
class CreateExpenseUseCase(
    private val eventSendClient: EventSendClient,
) {
    suspend fun execute(createExpenseParam: CreateExpenseParam): CreateExpenseDto =
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
    val localDate: LocalDate,
    val memo: String,
) {
    fun toModel(): Pair<Snowflake, Expense> =
        Pair(
            first = messageId,
            second = Expense(
                amount = amount,
                category = category,
                payer = payer,
                localDate = localDate,
                memo = """
                    ${localDate.month.number}/${localDate.day}
                    $memo
                """.trimIndent(),
            )
        )
}

data class CreateExpenseDto(
    val amount: Int,
    val category: String,
    val payer: String,
    val localDate: LocalDate,
    val memo: String,
) {
    companion object {
        fun from(expense: Expense): CreateExpenseDto =
            CreateExpenseDto(
                amount = expense.amount,
                category = expense.category,
                payer = expense.payer,
                localDate = expense.localDate,
                memo = expense.memo,
            )
    }
}
