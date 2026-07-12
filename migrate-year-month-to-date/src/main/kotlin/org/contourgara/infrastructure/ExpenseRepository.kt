package org.contourgara.infrastructure

import org.contourgara.domain.OldExpense
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.select

fun selectOldExpense(): List<OldExpense> =
    ExpenseIdTable
        .innerJoin(otherTable = ExpenseYearTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseYearTable.expenseId })
        .innerJoin(otherTable = ExpenseMonthTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseMonthTable.expenseId })
        .innerJoin(otherTable = ExpenseMemoTable, onColumn = { ExpenseIdTable.expenseId }, otherColumn = { ExpenseMemoTable.expenseId })
        .select(
            ExpenseIdTable.expenseId,
            ExpenseYearTable.year,
            ExpenseMonthTable.month,
            ExpenseMemoTable.memo,
        )
        .orderBy(column = ExpenseIdTable.expenseId, order = SortOrder.ASC)
        .map {
            OldExpense(
                id = it[ExpenseIdTable.expenseId],
                year = it[ExpenseYearTable.year],
                month = it[ExpenseMonthTable.month],
                memo = it[ExpenseMemoTable.memo],
            )
        }

object ExpenseIdTable : Table("expense_id") {
    val expenseId = varchar("expense_id", 26)
}

object ExpenseYearTable : Table("expense_year") {
    val expenseId = varchar("expense_id", 26)
    val year = integer("year")
}

object ExpenseMonthTable : Table("expense_month") {
    val expenseId = varchar("expense_id", 26)
    val month = integer("month")
}

object ExpenseMemoTable : Table("expense_memo") {
    val expenseId = varchar("expense_id", 26)
    val memo = varchar("memo", 100)
}
