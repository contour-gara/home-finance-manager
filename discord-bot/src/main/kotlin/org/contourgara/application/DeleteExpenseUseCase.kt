package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class DeleteExpenseUseCase(
    private val eventSendClient: EventSendClient,
) {
    fun execute(
        messageId: Snowflake,
    ): Snowflake =
        messageId
            .also { eventSendClient.deleteExpense(messageId = it) }
}
