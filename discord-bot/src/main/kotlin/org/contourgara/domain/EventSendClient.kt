package org.contourgara.domain

import dev.kord.common.entity.Snowflake

interface EventSendClient {
    fun registerBill(bill: Bill)
    fun deleteBill(billId: BillId)
    fun showBalance(lender: User, borrower: User)
    fun createExpense(messageId: Snowflake, expense: Expense)
    fun deleteExpense(createMessageId: Snowflake, deleteMessageId: Snowflake)
}
