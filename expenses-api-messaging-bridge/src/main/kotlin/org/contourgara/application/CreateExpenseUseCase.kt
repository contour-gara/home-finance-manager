package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.ExpenseIdRepository
import org.contourgara.domain.MessageClient
import org.contourgara.domain.MessageId
import org.contourgara.domain.ProcessedMessageIdRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

class CreateExpenseUseCase(
    private val processedMessageIdRepository: ProcessedMessageIdRepository,
    private val expenseClient: ExpenseClient,
    private val expenseIdRepository: ExpenseIdRepository,
    private val messageClient: MessageClient,
) {
    fun execute(param: CreateExpenseParam) =
        transaction {
            param
                .toModel()
                .also { (messageId, _) ->
                    println("execute usecase")
                    processedMessageIdRepository
                        .save(messageId = messageId)
                }
                .let { (messageId, expense) ->
                    val (expenseId, expenseEventId) = expenseClient
                        .create(expense = expense)
                    Triple(first = messageId, second = expenseId, third = expenseEventId)
                }
                .also { (messageId, expenseId, _) ->
                    expenseIdRepository
                        .save(expenseId = expenseId, messageId = messageId)
                }
                .also { (messageId, expenseId, expenseEventId) ->
                    messageClient
                        .reply(messageId = messageId, expenseId = expenseId, expenseEventId = expenseEventId)
                }
                .let { Unit }
        }
}

data class CreateExpenseParam(
    private val messageId: String,
    private val expenseId: String,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
) {
    fun toModel(): Pair<MessageId, Expense> =
        Pair(
            first = MessageId(value = Snowflake(value = messageId)),
            second = Expense(
                expenseId = ExpenseId(value = ULID.parseULID(ulidString = expenseId)),
                amount = amount,
                payer = payer,
                category = category,
                year = year,
                month = month,
                memo = memo,
            )
        )
}
