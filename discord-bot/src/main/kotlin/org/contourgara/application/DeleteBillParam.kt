package org.contourgara.application

import org.contourgara.domain.BillId
import ulid.ULID

data class DeleteBillParam(
    private val billId: ULID,
    val registerBillMessageId: String,
    ) {
    fun toModel() = BillId(billId)
}
