package org.contourgara.domain

interface ExpenseIdRepository {
    fun save(expenseId: ExpenseId, messageId: MessageId)
    fun findByMessageId(messageId: MessageId): ExpenseId?
}
