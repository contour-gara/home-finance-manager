package org.contourgara.infrastructure.repository

import org.contourgara.domain.Amount
import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Memo
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select

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
                        it[year] = expense.year.value
                    }
                ExpenseMonthTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[month] = expense.month.value
                    }
                ExpenseMemoTable
                    .insert {
                        it[expenseId] = expense.expenseId.value.toString()
                        it[memo] = expense.memo.value
                    }
            }

    override fun findById(expenseId: ExpenseId): Expense? =
        ExpenseIdTable
            .innerJoin(otherTable = ExpenseAmountTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseAmountTable.expenseId })
            .innerJoin(otherTable = ExpensePayerTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpensePayerTable.expenseId })
            .innerJoin(otherTable = ExpenseCategoryTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseCategoryTable.expenseId })
            .innerJoin(otherTable = ExpenseYearTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseYearTable.expenseId })
            .innerJoin(otherTable = ExpenseMonthTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseMonthTable.expenseId })
            .innerJoin(otherTable = ExpenseMemoTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseMemoTable.expenseId })
            .select(
                ExpenseIdTable.expenseId,
                ExpenseAmountTable.amount,
                ExpensePayerTable.payer,
                ExpenseCategoryTable.category,
                ExpenseYearTable.year,
                ExpenseMonthTable.month,
                ExpenseMemoTable.memo,
            )
            .where { ExpenseIdTable.expenseId eq expenseId.value.toString() }
            .orderBy(column = ExpenseIdTable.expenseId, order = SortOrder.DESC)
            .limit(count = 1)
            .singleOrNull()
            ?.let {
                Expense(
                    expenseId = ExpenseId(value = it[ExpenseIdTable.expenseId]),
                    amount = Amount(value = it[ExpenseAmountTable.amount]),
                    payer = Payer.valueOf(value = it[ExpensePayerTable.payer]),
                    category = Category.valueOf(value = it[ExpenseCategoryTable.category]),
                    year = Year.of(value = it[ExpenseYearTable.year]),
                    month = Month.of(value = it[ExpenseMonthTable.month]),
                    memo = Memo(value = it[ExpenseMemoTable.memo]),
                )
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
