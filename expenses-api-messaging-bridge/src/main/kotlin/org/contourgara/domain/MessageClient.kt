package org.contourgara.domain

interface MessageClient {
    fun replySuccessCreateExpense(messageId: MessageId, expenseId: ExpenseId, expenseEventId: ExpenseEventId)
}
