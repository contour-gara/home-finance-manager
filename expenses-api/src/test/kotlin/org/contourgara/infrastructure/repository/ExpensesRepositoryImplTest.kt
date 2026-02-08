package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.Amount
import org.contourgara.domain.Category
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
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
        DbTestHelper.migrateAndConnect(mysql)
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
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ")
                values("01KDJN90ABDQNYYY300153QFE9")
                values("01KDJN9CGGE9G24AZTQHQKP97T")
                values("01KDJNKH74MRSVZJGVBG1PJA4V")
            }
            insertInto("expenses_year") {
                columns("last_event_id", "year")
                values("01KD27JEZQQY88RG18034YZHBV", 2026)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 2026)
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", 2027)
                values("01KDJN90ABDQNYYY300153QFE9", 2026)
                values("01KDJN9CGGE9G24AZTQHQKP97T", 2026)
                values("01KDJNKH74MRSVZJGVBG1PJA4V", 2026)
            }
            insertInto("expenses_month") {
                columns("last_event_id", "month")
                values("01KD27JEZQQY88RG18034YZHBV", 1)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 1)
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", 1)
                values("01KDJN90ABDQNYYY300153QFE9", 2)
                values("01KDJN9CGGE9G24AZTQHQKP97T", 1)
                values("01KDJNKH74MRSVZJGVBG1PJA4V", 1)
            }
            insertInto("expenses_payer") {
                columns("last_event_id", "payer")
                values("01KD27JEZQQY88RG18034YZHBV", "DIRECT_DEBIT")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", "DIRECT_DEBIT")
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", "DIRECT_DEBIT")
                values("01KDJN90ABDQNYYY300153QFE9", "DIRECT_DEBIT")
                values("01KDJN9CGGE9G24AZTQHQKP97T", "GARA")
                values("01KDJNKH74MRSVZJGVBG1PJA4V", "DIRECT_DEBIT")
            }
            insertInto("expenses_category") {
                columns("last_event_id", "category")
                values("01KD27JEZQQY88RG18034YZHBV", "RENT")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", "RENT")
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", "RENT")
                values("01KDJN90ABDQNYYY300153QFE9", "RENT")
                values("01KDJN9CGGE9G24AZTQHQKP97T", "RENT")
                values("01KDJNKH74MRSVZJGVBG1PJA4V", "UTILITIES")
            }
            insertInto("expenses_amount") {
                columns("last_event_id", "amount")
                values("01KD27JEZQQY88RG18034YZHBV", 1000)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 1500)
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", 1000)
                values("01KDJN90ABDQNYYY300153QFE9", 1000)
                values("01KDJN9CGGE9G24AZTQHQKP97T", 1000)
                values("01KDJNKH74MRSVZJGVBG1PJA4V", 1000)
            }
        }.launch()

        // execute
        val actual = transaction { sut.findLatestExpenses(Year._2026, Month.JANUARY, Payer.DIRECT_DEBIT, Category.RENT) }

        // assert
        val expected = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1500),
        )

        actual shouldBe expected

        assertThat(expensesYearTable)
            .hasNumberOfRows(6)
        assertThat(expensesMonthTable)
            .hasNumberOfRows(6)
        assertThat(expensesPayerTable)
            .hasNumberOfRows(6)
        assertThat(expensesCategoryTable)
            .hasNumberOfRows(6)
        assertThat(expensesAmountTable)
            .hasNumberOfRows(6)
    }

    test("支出合計を保存できる") {
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
            }
        }.launch()

        val expenses = Expenses(
            lastEventId = ExpenseEventId(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1000),
        )

        // execute
        val actual = transaction { sut.save(expenses) }

        // assert
        val expected = expenses
        actual shouldBe expected

        assertThat(expensesYearTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("last_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("year").isEqualTo(2026)

        assertThat(expensesMonthTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("last_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("month").isEqualTo(1)

        assertThat(expensesPayerTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("last_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("payer").isEqualTo("DIRECT_DEBIT")

        assertThat(expensesCategoryTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("last_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("category").isEqualTo("RENT")

        assertThat(expensesAmountTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("last_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("amount").isEqualTo(1000)
    }

    test("支出合計を年月で検索できる") {
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
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ")
                values("01KDJN90ABDQNYYY300153QFE9")
                values("01KDJN9CGGE9G24AZTQHQKP97T")
                values("01KDJNKH74MRSVZJGVBG1PJA4V")
            }
            insertInto("expenses_year") {
                columns("last_event_id", "year")
                values("01KD27JEZQQY88RG18034YZHBV", 2026)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 2026)
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", 2027)
                values("01KDJN90ABDQNYYY300153QFE9", 2026)
                values("01KDJN9CGGE9G24AZTQHQKP97T", 2026)
                values("01KDJNKH74MRSVZJGVBG1PJA4V", 2026)
            }
            insertInto("expenses_month") {
                columns("last_event_id", "month")
                values("01KD27JEZQQY88RG18034YZHBV", 1)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 1)
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", 1)
                values("01KDJN90ABDQNYYY300153QFE9", 2)
                values("01KDJN9CGGE9G24AZTQHQKP97T", 1)
                values("01KDJNKH74MRSVZJGVBG1PJA4V", 1)
            }
            insertInto("expenses_payer") {
                columns("last_event_id", "payer")
                values("01KD27JEZQQY88RG18034YZHBV", "DIRECT_DEBIT")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", "DIRECT_DEBIT")
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", "DIRECT_DEBIT")
                values("01KDJN90ABDQNYYY300153QFE9", "DIRECT_DEBIT")
                values("01KDJN9CGGE9G24AZTQHQKP97T", "GARA")
                values("01KDJNKH74MRSVZJGVBG1PJA4V", "DIRECT_DEBIT")
            }
            insertInto("expenses_category") {
                columns("last_event_id", "category")
                values("01KD27JEZQQY88RG18034YZHBV", "RENT")
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", "RENT")
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", "RENT")
                values("01KDJN90ABDQNYYY300153QFE9", "RENT")
                values("01KDJN9CGGE9G24AZTQHQKP97T", "RENT")
                values("01KDJNKH74MRSVZJGVBG1PJA4V", "UTILITIES")
            }
            insertInto("expenses_amount") {
                columns("last_event_id", "amount")
                values("01KD27JEZQQY88RG18034YZHBV", 1000)
                values("01KDHVD5XTTR9XR4ZAFSSETGXS", 1500)
                values("01KDJN6R1CRJNRCCS0DMAMTTFZ", 1000)
                values("01KDJN90ABDQNYYY300153QFE9", 1000)
                values("01KDJN9CGGE9G24AZTQHQKP97T", 1000)
                values("01KDJNKH74MRSVZJGVBG1PJA4V", 1000)
            }
        }.launch()

        // execute
        val actual = transaction { sut.findMonthlyExpenses(Year._2026, Month.JANUARY) }

        // assert
        val expected = listOf(
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJNKH74MRSVZJGVBG1PJA4V")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.UTILITIES,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJN9CGGE9G24AZTQHQKP97T")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.GARA,
                category = Category.RENT,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1500),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1000),
            ),
        )

        actual shouldBe expected

        assertThat(expensesYearTable)
            .hasNumberOfRows(6)
        assertThat(expensesMonthTable)
            .hasNumberOfRows(6)
        assertThat(expensesPayerTable)
            .hasNumberOfRows(6)
        assertThat(expensesCategoryTable)
            .hasNumberOfRows(6)
        assertThat(expensesAmountTable)
            .hasNumberOfRows(6)
    }
})
