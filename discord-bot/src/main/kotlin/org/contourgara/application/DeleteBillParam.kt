package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.BillId
import ulid.ULID

data class DeleteBillParam(
    private val billId: ULID,
    val registerBillMessageId: Snowflake,
) {
    fun toModel() = BillId(billId)
}
