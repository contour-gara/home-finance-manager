package org.contourgara.infrastructure

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import kotlinx.datetime.LocalDate
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.NewExpense
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer

class ExpenseRepositoryTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = install(ext = TestContainerSpecExtension(container = MySQLContainer("mysql:8.0.43-oraclelinux9")))
        .apply { startupAttempts = 1 }

    beforeSpec {
        Flyway
            .configure()
            .dataSource(
                mysql.jdbcUrl,
                mysql.username,
                mysql.password,
            )
            .driver("com.mysql.cj.jdbc.Driver")
            .load()
            .migrate()

        Database.connect(
            url = mysql.jdbcUrl + "?rewriteBatchedStatements=TRUE&profileSQL=true",
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql.username,
            password = mysql.password,
        )

        assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
    }

    beforeTest {
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
        ) {
            deleteAllFrom(
                "expense_year",
                "expense_month",
                "expense_memo",
                "expense_date",
                "expense_id",
            )
        }.launch()
    }

    context("日付の保存") {
        test("日付を保存できる") {
            // setup
            dbSetup(
                to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
            ) {
                insertInto("expense_id") {
                    columns("expense_id")
                    values("01K4MXEKC0PMTJ8FA055N4SH79")
                    values("01K4MXEKC0PMTJ8FA055N4SH80")
                    values("01K4MXEKC0PMTJ8FA055N4SH81")
                }
            }
                .launch()

            val expenseIdTable = assertDbConnection.table("expense_id").build()
            val expenseDateTable = assertDbConnection.table("expense_date").build()

            val newExpenses = listOf(
                NewExpense(id = "01K4MXEKC0PMTJ8FA055N4SH79", date = LocalDate(year = 2026, month = 1, day = 1)),
                NewExpense(id = "01K4MXEKC0PMTJ8FA055N4SH80", date = LocalDate(year = 2026, month = 1, day = 2)),
                NewExpense(id = "01K4MXEKC0PMTJ8FA055N4SH81", date = LocalDate(year = 2027, month = 2, day = 3)),
            )

            // execute
            transaction {
                saveNewExpenses(newExpenses = newExpenses)
            }

            // assert
            assertThat(expenseIdTable)
                .hasNumberOfRows(3)
                .row(0)
                .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
                .row(1)
                .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH80")
                .row(2)
                .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH81")
            assertThat(expenseDateTable)
                .hasNumberOfRows(3)
                .row(0)
                .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
                .value("date").isEqualTo("2026-01-01")
                .row(1)
                .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH80")
                .value("date").isEqualTo("2026-01-02")
                .row(2)
                .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH81")
                .value("date").isEqualTo("2027-02-03")
        }
    }
})