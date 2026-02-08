package org.contourgara.infrastructure.repository

import org.contourgara.domain.Amount
import org.contourgara.domain.Category
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import ulid.ULID

object ExpensesRepositoryImpl : ExpensesRepository {
    override fun findLatestExpenses(
        year: Year,
        month: Month,
        payer: Payer,
        category: Category
    ): Expenses? =
        ExpenseEventIdTable
            .innerJoin(otherTable = ExpensesYearTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpensesYearTable.lastEventId })
            .innerJoin(otherTable = ExpensesMonthTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpensesMonthTable.lastEventId })
            .innerJoin(otherTable = ExpensesPayerTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpensesPayerTable.lastEventId })
            .innerJoin(otherTable = ExpensesCategoryTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpensesCategoryTable.lastEventId })
            .innerJoin(otherTable = ExpensesAmountTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpensesAmountTable.lastEventId })
            .select(
                ExpenseEventIdTable.expenseEventId,
                ExpensesYearTable.year,
                ExpensesMonthTable.month,
                ExpensesPayerTable.payer,
                ExpensesCategoryTable.category,
                ExpensesAmountTable.amount,
            )
            .where {
                (ExpensesYearTable.year eq year.value)
                    .and(op = ExpensesMonthTable.month eq month.value)
                    .and(op = ExpensesPayerTable.payer eq payer.name)
                    .and(op = ExpensesCategoryTable.category eq category.name)
            }
            .orderBy(column = ExpenseEventIdTable.expenseEventId, order = SortOrder.DESC)
            .limit(count = 1)
            .singleOrNull()
            ?.let {
                Expenses(
                    lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = it[ExpenseEventIdTable.expenseEventId])),
                    year = Year.of(value = it[ExpensesYearTable.year]),
                    month = Month.of(value = it[ExpensesMonthTable.month]),
                    payer = Payer.valueOf(value = it[ExpensesPayerTable.payer]),
                    category = Category.valueOf(value = it[ExpensesCategoryTable.category]),
                    amount = Amount(value = it[ExpensesAmountTable.amount]),
                )
            }

    override fun save(expenses: Expenses): Expenses =
        expenses
            .also {
                ExpensesYearTable
                    .insert {
                        it[lastEventId] = expenses.lastEventId.value.toString()
                        it[year] = expenses.year.value
                    }
                ExpensesMonthTable
                    .insert {
                        it[lastEventId] = expenses.lastEventId.value.toString()
                        it[month] = expenses.month.value
                    }
                ExpensesPayerTable
                    .insert {
                        it[lastEventId] = expenses.lastEventId.value.toString()
                        it[payer] = expenses.payer.name
                    }
                ExpensesCategoryTable
                    .insert {
                        it[lastEventId] = expenses.lastEventId.value.toString()
                        it[category] = expenses.category.name
                    }
                ExpensesAmountTable
                    .insert {
                        it[lastEventId] = expenses.lastEventId.value.toString()
                        it[amount] = expenses.amount.value
                    }
            }
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
