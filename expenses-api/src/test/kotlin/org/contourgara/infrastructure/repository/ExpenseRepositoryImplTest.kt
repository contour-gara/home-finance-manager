package org.contourgara.infrastructure.repository

import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.configuration.DBUnitConfig
import com.github.database.rider.core.configuration.DataSetConfig
import com.github.database.rider.core.dsl.RiderDSL
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.AppConfig
import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Payer
import org.contourgara.migration
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import ulid.ULID
import java.sql.DriverManager

class ExpenseRepositoryImplTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }

    beforeSpec {
        mysql.start()

        val appConfig = mockk<AppConfig>()
        every { appConfig.datasourceUrl } returns mysql.jdbcUrl
        every { appConfig.datasourceUser } returns mysql.username
        every { appConfig.datasourcePassword } returns mysql.password
        migration(appConfig)

        Database.connect(
            url = mysql.jdbcUrl,
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql.username,
            password = mysql.password,
        )
    }

    test("支出情報保存メソッドが、支出 ID、金額、支払い者、支出カテゴリー、メモを保存し、支出 ID を返す") {
        // setup
        RiderDSL.withConnection(
            DriverManager.getConnection(mysql.jdbcUrl, mysql.username, mysql.password)
        )
        RiderDSL.DataSetConfigDSL
            .withDataSetConfig(DataSetConfig("expense_0.yaml"))
        RiderDSL.DBUnitConfigDSL
            .withDBUnitConfig(
                DBUnitConfig().caseInsensitiveStrategy(Orthography.LOWERCASE)
            )
            .createDataSet()

        val expenses = Expense(
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            amount = 1000,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            memo = "test",
        )

        val sut = ExpenseRepositoryImpl

        // execute
        val actual = transaction { sut.create(expenses) }

        // assert
        val expected = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))
        actual shouldBe expected

        RiderDSL.DataSetConfigDSL
            .withDataSetConfig(DataSetConfig("expense_1.yaml"))
        RiderDSL.DBUnitConfigDSL.expectDataSet()
    }
})
