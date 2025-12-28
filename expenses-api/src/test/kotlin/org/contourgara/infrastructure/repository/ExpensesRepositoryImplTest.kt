package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.Category
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import ulid.ULID

class ExpensesRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val sut = ExpensesRepositoryImpl

    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }

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

    test("支出合計が存在しない場合、null を返す") {
        // setup
        val expensesYearTable = assertDbConnection.table("expenses_year").build()
        val expensesMonthTable = assertDbConnection.table("expenses_month").build()
        val expensesPayerTable = assertDbConnection.table("expenses_payer").build()
        val expensesCategoryTable = assertDbConnection.table("expenses_category").build()
        val expensesAmountTable = assertDbConnection.table("expenses_amount").build()

        // execute
        val actual = transaction { sut.findLatestExpenses(Year._2026, Month.JANUARY, Payer.DIRECT_DEBIT, Category.RENT) }

        // assert
        actual.shouldBeNull()

        assertThat(expensesYearTable)
            .hasNumberOfRows(0)
        assertThat(expensesMonthTable)
            .hasNumberOfRows(0)
        assertThat(expensesPayerTable)
            .hasNumberOfRows(0)
        assertThat(expensesCategoryTable)
            .hasNumberOfRows(0)
        assertThat(expensesAmountTable)
            .hasNumberOfRows(0)
    }

    test("支出合計が存在する場合、最新の支払合計を返す") {
        // setup
        val expensesYearTable = assertDbConnection.table("expenses_year").build()
        val expensesMonthTable = assertDbConnection.table("expenses_month").build()
        val expensesPayerTable = assertDbConnection.table("expenses_payer").build()
        val expensesCategoryTable = assertDbConnection.table("expenses_category").build()
        val expensesAmountTable = assertDbConnection.table("expenses_amount").build()

        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password)
        ) {
            insertInto("expense_event_id") {
                columns("expense_event_id")
                values("01KD27JEZQQY88RG18034YZHBV")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS")
            }
            insertInto("expenses_year") {
                columns("last_event_id", "year")
                values("01KD27JEZQQY88RG18034YZHBV", 2026)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 2026)
            }
            insertInto("expenses_month") {
                columns("last_event_id", "month")
                values("01KD27JEZQQY88RG18034YZHBV", 1)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 1)
            }
            insertInto("expenses_payer") {
                columns("last_event_id", "payer")
                values("01KD27JEZQQY88RG18034YZHBV", "DIRECT_DEBIT")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", "DIRECT_DEBIT")
            }
            insertInto("expenses_category") {
                columns("last_event_id", "category")
                values("01KD27JEZQQY88RG18034YZHBV", "RENT")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", "RENT")
            }
            insertInto("expenses_amount") {
                columns("last_event_id", "amount")
                values("01KD27JEZQQY88RG18034YZHBV", 1000)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 1500)
            }
        }.launch()

        // execute
        val actual = transaction { sut.findLatestExpenses(Year._2026, Month.JANUARY, Payer.DIRECT_DEBIT, Category.RENT) }

        // assert
        val expected = Expenses(ExpenseEventId(ULID.parseULID("01KDHVD5XTTR9XR4ZAFSSETGXS")), Year._2026, Month.JANUARY, Payer.DIRECT_DEBIT, Category.RENT, 1500)
        actual shouldBe expected

        assertThat(expensesYearTable)
            .hasNumberOfRows(2)
        assertThat(expensesMonthTable)
            .hasNumberOfRows(2)
        assertThat(expensesPayerTable)
            .hasNumberOfRows(2)
        assertThat(expensesCategoryTable)
            .hasNumberOfRows(2)
        assertThat(expensesAmountTable)
            .hasNumberOfRows(2)
    }
})
