package org.contourgara.domain

data class ExpenseEvent(
    val expenseEventID: ExpenseEventID,
    val expenseId: ExpenseId,
    val eventCategory: EventCategory,
)
