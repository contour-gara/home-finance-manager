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
                .let { (createMessageId, deleteMessageId) ->
                    Pair(
                        first = deleteMessageId,
                        second = expenseIdRepository.findByMessageId(messageId = createMessageId),
                    )
                }
                .let { (deleteMessageId, expenseId) ->
                    Triple(
                        first = deleteMessageId,
                        second = expenseId,
                        third = expenseClient.delete(expenseId = expenseId),
                    )
                }
                .let { (deleteMessageId, expenseId, expenseEventId) ->
                    messageClient.replySuccessDeleteExpense(messageId = deleteMessageId, expenseId = expenseId, expenseEventId = expenseEventId)
                }
                .let { Unit }
        }
}

data class DeleteExpenseParam(
    private val createMessageId: String,
    private val deleteMessageId: String,
) {
    fun toModel(): Pair<MessageId, MessageId> =
        Pair(
            first = MessageId(value = Snowflake(value = createMessageId)),
            second = MessageId(value = Snowflake(value = deleteMessageId)),
        )
}
