package org.contourgara.domain

import ulid.ULID

interface ExpenseRepository {
    fun create(expense: Expense): ULID
}
