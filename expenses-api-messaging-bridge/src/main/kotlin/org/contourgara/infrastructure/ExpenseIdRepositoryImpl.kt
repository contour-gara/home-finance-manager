package org.contourgara.infrastructure

import org.contourgara.domain.ExpenseId
import org.contourgara.domain.ExpenseIdRepository
import org.contourgara.domain.MessageId
import org.jetbrains.exposed.v1.jdbc.insert

object ExpenseIdRepositoryImpl : ExpenseIdRepository {
    override fun save(expenseId: ExpenseId, messageId: MessageId) =
        ExpenseIdTable
            .insert {
                it[expenseIdColumn] = expenseId.value.toString()
                it[messageIdColumn] = messageId.value.toString()
            }
            .let { Unit }

    override fun findByMessageId(messageId: MessageId): ExpenseId? {
        TODO("Not yet implemented")
    }
}
