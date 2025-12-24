package org.contourgara.presentation

import kotlinx.serialization.Serializable
import ulid.ULID

@Serializable
data class CreateExpenseResponse(
    private val expenseId: ULID,
    private val expenseEventId: ULID,
)
