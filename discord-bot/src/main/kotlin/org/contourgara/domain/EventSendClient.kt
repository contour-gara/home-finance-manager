package org.contourgara.domain

import dev.kord.common.entity.Snowflake

interface EventSendClient {
    suspend fun registerBill(bill: Bill)
    suspend fun deleteBill(billId: BillId)
    suspend fun showBalance(lender: User, borrower: User)
    suspend fun createExpense(messageId: Snowflake, expense: Expense)
    suspend fun deleteExpense(createMessageId: Snowflake, deleteMessageId: Snowflake)
}
