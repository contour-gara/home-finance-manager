package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.Amount
import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Memo
import org.contourgara.domain.Payer
import org.contourgara.domain.ValidatedDate
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer
import ulid.ULID

class ExpenseRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = install(ext = TestContainerSpecExtension(container = MySQLContainer("mysql:8.0.43-oraclelinux9")))
        .apply { startupAttempts = 1 }

    val sut = ExpenseRepositoryImpl

    beforeSpec {
        DbTestHelper.migrateAndConnect(mysql)
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
        val expenseDateTable = assertDbConnection.table("expense_date").build()
        val expenseMemoTable = assertDbConnection.table("expense_memo").build()

        val expenses = Expense(
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            amount = Amount(value = 1000),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            date = ValidatedDate(value = LocalDate(year = 2026, month = 1, day = 1)),
            memo = Memo(value = "test"),
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
        assertThat(expenseDateTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("date").isEqualTo("2026-01-01")
        assertThat(expenseMemoTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("memo").isEqualTo("test")
    }

    test("支出 ID 検索で、該当のレコードがない場合、null を返す") {
        // setup
        val expenseIdTable = assertDbConnection.table("expense_id").build()
        val expenseAmountTable = assertDbConnection.table("expense_amount").build()
        val expensePayerTable = assertDbConnection.table("expense_payer").build()
        val expenseCategoryTable = assertDbConnection.table("expense_category").build()
        val expenseDateTable = assertDbConnection.table("expense_date").build()
        val expenseMemoTable = assertDbConnection.table("expense_memo").build()

        val expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))

        // execute
        val actual = transaction { sut.findById(expenseId = expenseId) }

        // assert
        actual.shouldBeNull()

        assertThat(expenseIdTable).hasNumberOfRows(0)
        assertThat(expenseAmountTable).hasNumberOfRows(0)
        assertThat(expensePayerTable).hasNumberOfRows(0)
        assertThat(expenseCategoryTable).hasNumberOfRows(0)
        assertThat(expenseDateTable).hasNumberOfRows(0)
        assertThat(expenseMemoTable).hasNumberOfRows(0)
    }

    test("支出 ID 検索で、該当のレコードがある場合、Expense を返す") {
        // setup
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
        ) {
            insertInto("expense_id") {
                columns("expense_id")
                values("01K4MXEKC0PMTJ8FA055N4SH79")
            }
            insertInto("expense_amount") {
                columns("expense_id", "amount")
                values("01K4MXEKC0PMTJ8FA055N4SH79", 1000)
            }
            insertInto("expense_payer") {
                columns("expense_id", "payer")
                values("01K4MXEKC0PMTJ8FA055N4SH79", "DIRECT_DEBIT")
            }
            insertInto("expense_category") {
                columns("expense_id", "category")
                values("01K4MXEKC0PMTJ8FA055N4SH79", "RENT")
            }
            insertInto("expense_date") {
                columns("expense_id", "date")
                values("01K4MXEKC0PMTJ8FA055N4SH79", "2026-01-01")
            }
            insertInto("expense_memo") {
                columns("expense_id", "memo")
                values("01K4MXEKC0PMTJ8FA055N4SH79", "test")
            }
        }
            .launch()

        val expenseIdTable = assertDbConnection.table("expense_id").build()
        val expenseAmountTable = assertDbConnection.table("expense_amount").build()
        val expensePayerTable = assertDbConnection.table("expense_payer").build()
        val expenseCategoryTable = assertDbConnection.table("expense_category").build()
        val expenseDateTable = assertDbConnection.table("expense_date").build()
        val expenseMemoTable = assertDbConnection.table("expense_memo").build()

        val expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))

        // execute
        val actual = transaction { sut.findById(expenseId = expenseId) }

        // assert
        val expected = Expense(
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            amount = Amount(value = 1000),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            date = ValidatedDate(value = LocalDate(year = 2026, month = 1, day = 1)),
            memo = Memo(value = "test"),
        )
        actual shouldBe expected

        assertThat(expenseIdTable).hasNumberOfRows(1)
        assertThat(expenseAmountTable).hasNumberOfRows(1)
        assertThat(expensePayerTable).hasNumberOfRows(1)
        assertThat(expenseCategoryTable).hasNumberOfRows(1)
        assertThat(expenseDateTable).hasNumberOfRows(1)
        assertThat(expenseMemoTable).hasNumberOfRows(1)
    }
})
