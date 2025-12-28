package org.contourgara.infrastructure.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.Category
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.testcontainers.containers.MySQLContainer

class ExpensesRepositoryImplTest : FunSpec({
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
    }

    beforeTest {
        DbTestHelper.deleteAllData(mysql.jdbcUrl, mysql.username, mysql.password)
    }

    test("支出合計が存在しない場合、null を返す") {
        val assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
        val expensesYearTable = assertDbConnection.table("expenses_year").build()
        val expensesMonthTable = assertDbConnection.table("expenses_month").build()
        val expensesPayerTable = assertDbConnection.table("expenses_payer").build()
        val expensesCategoryTable = assertDbConnection.table("expenses_category").build()
        val expensesAmountTable = assertDbConnection.table("expenses_amount").build()

        val sut = ExpensesRepositoryImpl

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
