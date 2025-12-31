package org.contourgara.infrastructure.repository

import org.contourgara.domain.Expense
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.insert

object ExpenseRepositoryImpl : ExpenseRepository {
    override fun create(expense: Expense): Expense =
        expense
            .also {
                ExpenseIdTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                    }
                ExpenseAmountTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[amount] = expense.amount.value
                    }
                ExpensePayerTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[payer] = expense.payer.name
                    }
                ExpenseCategoryTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[category] = expense.category.name
                    }
                ExpenseYearTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[year] = expense.year.intYear
                    }
                ExpenseMonthTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[month] = expense.month.intMonth
                    }
                ExpenseMemoTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[memo] = expense.memo
                    }
            }
}

private object ExpenseAmountTable : Table("expense_amount") {
    val expenseId = varchar("expense_id", 26)
    val amount = integer("amount")
}

private object ExpensePayerTable : Table("expense_payer") {
    val expenseId = varchar("expense_id", 26)
    val payer = varchar("payer", 100)
}

private object ExpenseCategoryTable : Table("expense_category") {
    val expenseId = varchar("expense_id", 26)
    val category = varchar("category", 100)
}

private object ExpenseYearTable : Table("expense_year") {
    val expenseId = varchar("expense_id", 26)
    val year = integer("year")
}

private object ExpenseMonthTable : Table("expense_month") {
    val expenseId = varchar("expense_id", 26)
    val month = integer("month")
}

private object ExpenseMemoTable : Table("expense_memo") {
    val expenseId = varchar("expense_id", 26)
    val memo = varchar("memo", 100)
}
