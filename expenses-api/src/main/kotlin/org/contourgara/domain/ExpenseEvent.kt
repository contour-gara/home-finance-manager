package org.contourgara.domain

import ulid.ULID

data class ExpenseEvent(
    val eventId: ULID,
    val expenseId: ExpenseId,
    val eventCategory: EventCategory,
)
