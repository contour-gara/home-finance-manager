package org.contourgara.infrastructure.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.assertj.db.type.Table
import org.contourgara.domain.Category
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.testcontainers.containers.MySQLContainer

class ExpensesRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection
    lateinit var expensesYearTable: Table
    lateinit var expensesMonthTable: Table
    lateinit var expensesPayerTable: Table
    lateinit var expensesCategoryTable: Table
    lateinit var expensesAmountTable: Table

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
        expensesYearTable = assertDbConnection.table("expenses_year").build()
        expensesMonthTable = assertDbConnection.table("expenses_month").build()
        expensesPayerTable = assertDbConnection.table("expenses_payer").build()
        expensesCategoryTable = assertDbConnection.table("expenses_category").build()
        expensesAmountTable = assertDbConnection.table("expenses_amount").build()
    }

    beforeTest {
        DbTestHelper.deleteAllData(mysql.jdbcUrl, mysql.username, mysql.password)
    }

    test("支出合計が存在しない場合、null を返す") {
        // execute
        val actual = sut.findLatestExpenses(Year._2026, Month.JANUARY, Payer.DIRECT_DEBIT, Category.RENT)

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
})
