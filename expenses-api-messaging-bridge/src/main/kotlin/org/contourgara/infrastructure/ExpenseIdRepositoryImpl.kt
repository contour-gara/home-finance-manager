package org.contourgara.infrastructure

import org.contourgara.domain.ExpenseId
import org.contourgara.domain.ExpenseIdRepository
import org.contourgara.domain.MessageId
import org.contourgara.infrastructure.ExpenseIdTable.expenseIdColumn
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import ulid.ULID

object ExpenseIdRepositoryImpl : ExpenseIdRepository {
    override fun save(expenseId: ExpenseId, messageId: MessageId) =
        ExpenseIdTable
            .insert {
                it[expenseIdColumn] = expenseId.value.toString()
                it[messageIdColumn] = messageId.value.toString()
            }
            .let { Unit }

    override fun findByMessageId(messageId: MessageId): ExpenseId =
        ExpenseIdTable
            .select(expenseIdColumn)
            .where {
                ExpenseIdTable.messageIdColumn eq messageId.value.toString()
            }
            .single()
            .let {
                ExpenseId(value = ULID.parseULID(ulidString = it[expenseIdColumn]))
            }
}
