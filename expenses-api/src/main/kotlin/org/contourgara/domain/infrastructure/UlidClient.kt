package org.contourgara.domain.infrastructure

import org.contourgara.domain.ExpenseEventID

interface UlidClient {
    fun nextUlid(): ExpenseEventID
}
