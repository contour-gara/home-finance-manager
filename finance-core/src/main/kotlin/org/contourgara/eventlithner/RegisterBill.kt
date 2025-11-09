package org.contourgara.eventlithner

data class RegisterBill(
    val billId: String,
    val amount: Int,
    val lender: User,
    val borrower: User,
    val memo: String,
)
