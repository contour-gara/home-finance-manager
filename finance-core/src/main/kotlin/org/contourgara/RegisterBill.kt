package org.contourgara

import kotlinx.serialization.Serializable
import ulid.ULID

@Serializable
data class RegisterBill(
    val billId: ULID,
    val amount: Int,
    val lender: User,
    val borrower: User,
    val memo: String,
)
