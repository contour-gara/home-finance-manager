package org.contourgara.application

data class RegisterBillParam(
    val amount: Int,
    val lender: String,
    val borrower: String,
    val memo: String
)
