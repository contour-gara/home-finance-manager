package org.contourgara.application

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.LocalDate
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.Expense
import org.contourgara.domain.SystemClock
import org.contourgara.domain.WrittenDate
import org.koin.core.annotation.Single

@Single
class CreateExpenseUseCase(
    private val eventSendClient: EventSendClient,
    private val systemClock: SystemClock,
) {
    suspend fun execute(createExpenseParam: CreateExpenseParam): CreateExpenseDto =
        createExpenseParam.toModel(submittedOn = systemClock.today())
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
    val year: Int?,
    val month: Int?,
    val day: Int?,
    val memo: String,
) {
    fun toModel(submittedOn: LocalDate): Pair<Snowflake, Expense> =
        Pair(
            first = messageId,
            second = Expense(
                amount = amount,
                category = category,
                payer = payer,
                localDate = WrittenDate.of(year = year, month = month, day = day).complete(submittedOn = submittedOn),
                memo = memo,
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
