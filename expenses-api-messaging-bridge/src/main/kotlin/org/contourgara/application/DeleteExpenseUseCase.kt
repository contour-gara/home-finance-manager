package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseIdRepository
import org.contourgara.domain.MessageClient
import org.contourgara.domain.MessageId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class DeleteExpenseUseCase(
    private val expenseIdRepository: ExpenseIdRepository,
    private val expenseClient: ExpenseClient,
    private val messageClient: MessageClient,
) {
    fun execute(param: DeleteExpenseParam) =
        transaction {
            param
                .toModel()
                .let { messageId ->
                    Pair(
                        first = messageId,
                        second = expenseIdRepository.findByMessageId(messageId = messageId),
                    )
                }
                .let { (messageId, expenseId) ->
                    Triple(
                        first = messageId,
                        second = expenseId,
                        third = expenseClient.delete(expenseId = expenseId),
                    )
                }
                .let { (messageId, expenseId, expenseEventId) ->
                    messageClient.replySuccessDeleteExpense(messageId = messageId, expenseId = expenseId, expenseEventId = expenseEventId)
                }
                .let { Unit }
        }
}

data class DeleteExpenseParam(
    private val messageId: String,
) {
    fun toModel(): MessageId = MessageId(value = Snowflake(value = messageId))
}
