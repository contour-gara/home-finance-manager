package org.contourgara.infrastructure.client

import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.infrastructure.UlidClient
import ulid.ULID

class UlidClientImpl : UlidClient {
    override fun nextUlid(): ExpenseEventId = ExpenseEventId(ULID.nextULID())
}
