package org.contourgara.application

import dev.kord.common.entity.Snowflake
import ulid.ULID

data class DeleteBillDto(
    val billId: ULID,
    val registerBillMessageId: Snowflake,
)
