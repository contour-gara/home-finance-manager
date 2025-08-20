package org.contourgara.application

data class RegisterBillDto(
    val id: String,
    val amount: Int,
    val claimant: String,
    val memo: String
)
