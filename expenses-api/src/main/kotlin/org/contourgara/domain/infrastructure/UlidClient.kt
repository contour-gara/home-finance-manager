package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEventId

interface UlidClient {
    fun nextUlid(): ExpenseEventId
}
