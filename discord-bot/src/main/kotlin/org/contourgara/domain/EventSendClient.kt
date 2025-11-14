package org.contourgara.domain

interface EventSendClient {
    fun registerBill(bill: Bill)
    fun deleteBill(billId: BillId)
    fun showBalance(lender: User, borrower: User)
}
