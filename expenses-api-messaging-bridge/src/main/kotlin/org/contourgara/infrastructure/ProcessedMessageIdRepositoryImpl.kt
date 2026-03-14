package org.contourgara.infrastructure

import org.contourgara.domain.MessageId
import org.contourgara.domain.ProcessedMessageIdRepository
import org.jetbrains.exposed.v1.jdbc.insert

object ProcessedMessageIdRepositoryImpl : ProcessedMessageIdRepository {
    override fun save(messageId: MessageId) =
        ProcessedMessageIdTable
            .insert {
                it[processedMessageId] = messageId.value.toString()
            }
            .let { Unit }
}
