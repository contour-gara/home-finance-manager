package org.contourgara.infrastructure.repository

import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.jetbrains.exposed.v1.jdbc.insert

object ExpenseRepositoryImpl : ExpenseRepository {
    override fun create(expense: Expense): ExpenseId =
        expense
            .expenseId
            .also {
                ExpenseIdTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                    }
                ExpenseAmountTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                        it[amount] = expense.amount
                    }
                ExpensePayerTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                        it[payer] = expense.payer.name
                    }
                ExpenseCategoryTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                        it[category] = expense.category.name
                    }
                ExpenseYearTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                        it[year] = expense.year.intYear
                    }
                ExpenseMonthTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                        it[month] = expense.month.intMonth
                    }
                ExpenseMemoTable
                    .insert {
                        it[expenseId] = expense.expenseId.id.toString()
                        it[memo] = expense.memo
                    }
            }
}
