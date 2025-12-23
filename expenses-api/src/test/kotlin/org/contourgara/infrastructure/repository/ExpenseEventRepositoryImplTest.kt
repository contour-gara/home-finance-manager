package org.contourgara.infrastructure.repository

import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.configuration.DBUnitConfig
import com.github.database.rider.core.configuration.DataSetConfig
import com.github.database.rider.core.dsl.RiderDSL
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.AppConfig
import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.migration
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import ulid.ULID
import java.sql.DriverManager

class ExpenseEventRepositoryImplTest : FunSpec({
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

    test("支出イベント保存メソッドが、イベント ID を保存し、Unit を返す") {
        // setup
        RiderDSL.withConnection(
            DriverManager.getConnection(mysql.jdbcUrl, mysql.username, mysql.password)
        )
        RiderDSL.DataSetConfigDSL
            .withDataSetConfig(DataSetConfig("expense_event_0.yaml"))
        RiderDSL.DBUnitConfigDSL
            .withDBUnitConfig(
                DBUnitConfig().caseInsensitiveStrategy(Orthography.LOWERCASE)
            )
            .createDataSet()

        val assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
        val expenseIdTable = assertDbConnection.table("expense_id").build()
        val expenseEventIdTable = assertDbConnection.table("expense_event_id").build()
        val expenseEventTable = assertDbConnection.table("expense_event").build()
        val expenseEventCategoryTable = assertDbConnection.table("expense_event_category").build()

        val expenseEvent = ExpenseEvent(
            expenseEventID = ExpenseEventId(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            eventCategory = EventCategory.CREATE,
        )

        val sut = ExpenseEventRepositoryImpl

        // execute
        val actual = transaction { sut.save(expenseEvent) }

        // assert
        val expected = Unit
        actual shouldBe expected

        assertThat(expenseIdTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
        assertThat(expenseEventIdTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
        assertThat(expenseEventTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
        assertThat(expenseEventCategoryTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_event_id").isEqualTo("01KD27JEZQQY88RG18034YZHBV")
            .value("event_category").isEqualTo("CREATE")
    }
})
