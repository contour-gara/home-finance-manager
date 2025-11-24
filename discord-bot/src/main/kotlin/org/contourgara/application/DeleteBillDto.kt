package org.contourgara.application

import ulid.ULID

data class DeleteBillDto(
    val billId: ULID,
    val amount: Int,
    val lender: String,
    val borrower: String,
    val memo: String,
    val registerBillMessageId: String,
)
