package org.contourgara.domain

import ulid.ULID

data class ExpenseEvent(
    val eventId: ULID,
    val expenseId: ULID,
    val eventCategory: EventCategory,
)
