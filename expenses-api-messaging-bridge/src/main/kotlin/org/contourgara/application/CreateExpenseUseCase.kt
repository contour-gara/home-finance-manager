package org.contourgara.application

import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseIdRepository
import org.contourgara.domain.MessageClient
import org.contourgara.domain.ProcessedMessageIdRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

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
