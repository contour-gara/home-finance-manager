package org.contourgara.domain.infrastructure

import arrow.core.Either
import org.contourgara.domain.Error
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseId

interface ExpenseEventRepository {
    fun save(expenseEvent: ExpenseEvent): ExpenseEvent
    fun findByExpenseId(expenseId: ExpenseId): Either<Error, ExpenseEvent>
}
