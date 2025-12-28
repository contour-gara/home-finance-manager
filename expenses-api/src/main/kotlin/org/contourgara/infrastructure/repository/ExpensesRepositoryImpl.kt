package org.contourgara.infrastructure.repository

import org.contourgara.domain.Category
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.select
import ulid.ULID

object ExpensesRepositoryImpl : ExpensesRepository {
    override fun findLatestExpenses(
        year: Year,
        month: Month,
        payer: Payer,
        category: Category
    ): Expenses? =
        ExpensesYearTable
            .innerJoin(otherTable = ExpensesMonthTable, onColumn = { ExpensesYearTable.lastEventId }, otherColumn = { ExpensesMonthTable.lastEventId })
            .innerJoin(otherTable = ExpensesPayerTable, onColumn = { ExpensesYearTable.lastEventId }, otherColumn = { ExpensesPayerTable.lastEventId })
            .innerJoin(otherTable = ExpensesCategoryTable, onColumn = { ExpensesYearTable.lastEventId }, otherColumn = { ExpensesCategoryTable.lastEventId })
            .innerJoin(otherTable = ExpensesAmountTable, onColumn = { ExpensesYearTable.lastEventId }, otherColumn = { ExpensesAmountTable.lastEventId })
            .select(
                ExpensesYearTable.lastEventId,
                ExpensesYearTable.year,
                ExpensesMonthTable.month,
                ExpensesPayerTable.payer,
                ExpensesCategoryTable.category,
                ExpensesAmountTable.amount,
            )
            .orderBy(ExpensesYearTable.lastEventId, SortOrder.DESC)
            .limit(1)
            .singleOrNull()
            ?.let {
                Expenses(
                    lastEventId = ExpenseEventId(id = ULID.parseULID(it[ExpensesYearTable.lastEventId])),
                    year = Year.of(intYear = it[ExpensesYearTable.year]),
                    month = Month.of(intMonth = it[ExpensesMonthTable.month]),
                    payer = Payer.valueOf(value = it[ExpensesPayerTable.payer]),
                    category = Category.valueOf(value = it[ExpensesCategoryTable.category]),
                    amount = it[ExpensesAmountTable.amount],
                )
            }

    override fun save(expenses: Expenses): Expenses = expenses
}

private object ExpensesYearTable : Table("expenses_year") {
    val lastEventId = varchar("last_event_id", 26)
    val year = integer("year")
}

private object ExpensesMonthTable : Table("expenses_month") {
    val lastEventId = varchar("last_event_id", 26)
    val month = integer("month")
}

private object ExpensesPayerTable : Table("expenses_payer") {
    val lastEventId = varchar("last_event_id", 26)
    val payer = varchar("payer", 100)
}

private object ExpensesCategoryTable : Table("expenses_category") {
    val lastEventId = varchar("last_event_id", 26)
    val category = varchar("category", 100)
}

private object ExpensesAmountTable : Table("expenses_amount") {
    val lastEventId = varchar("last_event_id", 26)
    val amount = integer("amount")
}
