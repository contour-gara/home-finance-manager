package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class DeleteExpenseUseCase(
    private val eventSendClient: EventSendClient,
) {
    suspend fun execute(
        createMessageId: Snowflake,
        deleteMessageId: Snowflake,
    ): Snowflake =
        createMessageId
            .also { eventSendClient.deleteExpense(createMessageId = it, deleteMessageId = deleteMessageId) }
}
