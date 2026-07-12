package org.contourgara

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun main() {
    Flyway
        .configure()
        .dataSource(
            System.getenv("EXPENSES_API_DATASOURCE_URL"),
            System.getenv("DATASOURCE_USERNAME"),
            System.getenv("DATASOURCE_PASSWORD"),
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()

    Database.connect(
        url = System.getenv("EXPENSES_API_DATASOURCE_URL"),
        driver = "com.mysql.cj.jdbc.Driver",
        user = System.getenv("DATASOURCE_USERNAME"),
        password = System.getenv("DATASOURCE_PASSWORD"),
    )

    transaction {
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
            .also {
                println(it.count())
            }
            .forEach {
                println(it)
            }
    }
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

data class OldExpense(
    val id: String,
    val year: Int,
    val month: Int,
    val memo: String,
)
