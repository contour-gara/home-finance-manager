package org.contourgara.domain

interface MessageClient {
    fun replySuccessCreateExpense(messageId: MessageId, expenseId: ExpenseId, expenseEventId: ExpenseEventId)
    fun replySuccessDeleteExpense(messageId: MessageId, expenseId: ExpenseId, expenseEventId: ExpenseEventId)
}
