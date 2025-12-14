package org.contourgara.application

import ulid.ULID

data class DeleteBillDto(
    val billId: ULID,
    val registerBillMessageId: String,
)
