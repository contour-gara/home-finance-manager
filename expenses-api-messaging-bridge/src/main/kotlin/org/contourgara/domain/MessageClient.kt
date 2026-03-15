package org.contourgara.domain

interface MessageClient {
    fun reply(messageId: MessageId, expenseId: ExpenseId, expenseEventId: ExpenseEventId)
}
