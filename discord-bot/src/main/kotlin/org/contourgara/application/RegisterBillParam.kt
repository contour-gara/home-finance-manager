package org.contourgara.application

data class RegisterBillParam(
    val amount: Int,
    val lender: String,
    val claimant: String,
    val memo: String
)
