package org.contourgara.infrastructure.repository

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseRepository
import org.jetbrains.exposed.v1.jdbc.insert
import ulid.ULID

class ExpenseRepositoryImpl : ExpenseRepository {
    override fun create(expense: Expense): ULID =
        expense
            .id
            .also {
                ExpenseId.insert { it[expenseId] = expense.id.toString() }
                ExpenseAmount.insert {
                    it[expenseId] = expense.id.toString()
                    it[amount] = expense.amount
                }
                ExpensePayer.insert {
                    it[expenseId] = expense.id.toString()
                    it[payer] = expense.payer.name
                }
                ExpenseCategory.insert {
                    it[expenseId] = expense.id.toString()
                    it[category] = expense.category.name
                }
                ExpenseMemo.insert {
                    it[expenseId] = expense.id.toString()
                    it[memo] = expense.memo
                }
            }
}
