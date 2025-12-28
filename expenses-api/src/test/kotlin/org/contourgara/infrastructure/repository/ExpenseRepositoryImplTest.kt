package org.contourgara.infrastructure.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import ulid.ULID

class ExpenseRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }

    val sut = ExpenseRepositoryImpl

    beforeSpec {
        mysql.start()

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
            url = mysql.jdbcUrl,
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql.username,
            password = mysql.password,
        )

        assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
    }

    beforeTest {
        DbTestHelper.deleteAllData(mysql.jdbcUrl, mysql.username, mysql.password)
    }

    test("支出情報保存メソッドが、支出 ID、金額、支払い者、支出カテゴリー、年、月、メモを保存し、支出 ID を返す") {
        // setup
        val expenseIdTable = assertDbConnection.table("expense_id").build()
        val expenseAmountTable = assertDbConnection.table("expense_amount").build()
        val expensePayerTable = assertDbConnection.table("expense_payer").build()
        val expenseCategoryTable = assertDbConnection.table("expense_category").build()
        val expenseYearTable = assertDbConnection.table("expense_year").build()
        val expenseMonthTable = assertDbConnection.table("expense_month").build()
        val expenseMemoTable = assertDbConnection.table("expense_memo").build()

        val expenses = Expense(
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            amount = 1000,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = "test",
        )

        // execute
        val actual = transaction { sut.create(expenses) }

        // assert
        val expected = expenses
        actual shouldBe expected

        assertThat(expenseIdTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
        assertThat(expenseAmountTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("amount").isEqualTo(1000)
        assertThat(expensePayerTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("payer").isEqualTo("DIRECT_DEBIT")
        assertThat(expenseCategoryTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("category").isEqualTo("RENT")
        assertThat(expenseYearTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("year").isEqualTo(2026)
        assertThat(expenseMonthTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("month").isEqualTo(1)
        assertThat(expenseMemoTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("memo").isEqualTo("test")
    }
})
