package org.contourgara.domain

import ulid.ULID

data class RegisterBill(
    val billId: ULID,
    val eventId: String,
    val lender: User,
    val borrower: User,
)
