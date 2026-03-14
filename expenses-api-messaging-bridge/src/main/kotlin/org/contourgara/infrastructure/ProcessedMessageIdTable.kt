package org.contourgara.infrastructure

import org.jetbrains.exposed.v1.core.Table

object ProcessedMessageIdTable : Table("processed_message_id") {
    val processedMessageId = varchar("message_id", 19)
}
