package org.contourgara.application

data class RegisterBillParam(
    val amount: Int,
    val claimant: String,
    val memo: String
)
