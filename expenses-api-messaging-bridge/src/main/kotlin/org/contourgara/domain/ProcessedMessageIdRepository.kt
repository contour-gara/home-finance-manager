package org.contourgara.domain

interface ProcessedMessageIdRepository {
    fun save(messageId: MessageId)
}
