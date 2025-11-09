package org.contourgara

data class RegisterBill(
    val billId: String,
    val amount: Int,
    val lender: User,
    val borrower: User,
    val memo: String,
)
