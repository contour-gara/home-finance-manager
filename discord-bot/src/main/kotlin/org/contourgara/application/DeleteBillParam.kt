package org.contourgara.application

import org.contourgara.domain.BillId
import ulid.ULID

data class DeleteBillParam(
    val billId: ULID,
) {
    fun toModel() = BillId(billId)
}
