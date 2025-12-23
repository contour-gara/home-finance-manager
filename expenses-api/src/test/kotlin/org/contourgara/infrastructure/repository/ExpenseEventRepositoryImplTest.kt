package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import ulid.ULID

class ExpenseEventRepositoryImplTest : FunSpec({
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
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
        ) {
            deleteAllFrom(
                "expense_id",
                "expense_amount",
                "expense_payer",
                "expense_category",
                "expense_memo",
                "expense_event_id",
                "expense_event",
                "expense_event_category",
            )
        }
            .launch()
    }

    test("支出イベント保存メソッドが、イベント ID を保存し、Unit を返す") {
        // setup
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
        ) {
            insertInto("expense_id") {
                columns("expense_id")
                values("01K4MXEKC0PMTJ8FA055N4SH79")
            }
        }
            .launch()

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
