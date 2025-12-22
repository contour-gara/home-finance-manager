package org.contourgara.infrastructure.client

import org.contourgara.domain.ExpenseEventID
import org.contourgara.domain.UlidClient
import ulid.ULID

class UlidClientImpl : UlidClient {
    override fun nextUlid(): ExpenseEventID = ExpenseEventID(ULID.nextULID())
}
