package org.contourgara.domain

data class ExpenseEvent(
    val expenseEventID: ExpenseEventId,
    val expenseId: ExpenseId,
    val eventCategory: EventCategory,
)
