package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.MessageId

class DeleteExpenseUseCase {
    fun execute(param: DeleteExpenseParam) {
    }
}

data class DeleteExpenseParam(
    private val messageId: String,
) {
    fun toModel(): MessageId = MessageId(value = Snowflake(value = messageId))
}
