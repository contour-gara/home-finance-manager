package org.contourgara.domain

data class ExpenseEvent(
    val expenseEventId: ExpenseEventId,
    val expenseId: ExpenseId,
    val eventCategory: EventCategory,
)
