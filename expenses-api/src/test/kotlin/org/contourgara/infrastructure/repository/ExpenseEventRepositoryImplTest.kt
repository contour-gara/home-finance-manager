package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import ulid.ULID

class ExpenseEventRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }

    val sut = ExpenseEventRepositoryImpl

    beforeSpec {
        mysql.start()
        DbTestHelper.migrateAndConnect(mysql)
        assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
    }

    beforeTest {
        DbTestHelper.deleteAllData(mysql.jdbcUrl, mysql.username, mysql.password)
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

        val expenseIdTable = assertDbConnection.table("expense_id").build()
        val expenseEventIdTable = assertDbConnection.table("expense_event_id").build()
        val expenseEventTable = assertDbConnection.table("expense_event").build()
        val expenseEventCategoryTable = assertDbConnection.table("expense_event_category").build()

        val expenseEvent = ExpenseEvent(
            expenseEventId = ExpenseEventId(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            eventCategory = EventCategory.CREATE,
        )

        // execute
        val actual = transaction { sut.save(expenseEvent) }

        // assert
        val expected = expenseEvent
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

    context("支出 ID 検索") {
        test("該当する支出 ID が存在しない場合、null を返す") {
            // setup
            val expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))

            // execute
            val actual = transaction { sut.findByExpenseId(expenseId = expenseId) }

            // assert
            actual.shouldBeNull()
        }

        test("該当する支出 ID が存在する場合、ExpenseEvent を返す") {
            // setup
            dbSetup(
                to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
            ) {
                insertInto("expense_id") {
                    columns("expense_id")
                    values("01K4MXEKC0PMTJ8FA055N4SH79")
                }
                insertInto("expense_event_id") {
                    columns("expense_event_id")
                    values("01KD27JEZQQY88RG18034YZHBV")
                    values("01KDHVD5XTTR9XR4ZAFSSETGXS")
                }
                insertInto("expense_event") {
                    columns("expense_event_id", "expense_id")
                    values("01KD27JEZQQY88RG18034YZHBV", "01K4MXEKC0PMTJ8FA055N4SH79")
                    values("01KDHVD5XTTR9XR4ZAFSSETGXS", "01K4MXEKC0PMTJ8FA055N4SH79")
                }
                insertInto("expense_event_category") {
                    columns("expense_event_id", "event_category")
                    values("01KD27JEZQQY88RG18034YZHBV", "CREATE")
                    values("01KDHVD5XTTR9XR4ZAFSSETGXS", "DELETE")
                }
            }
                .launch()

            val expenseIdTable = assertDbConnection.table("expense_id").build()
            val expenseEventIdTable = assertDbConnection.table("expense_event_id").build()
            val expenseEventTable = assertDbConnection.table("expense_event").build()
            val expenseEventCategoryTable = assertDbConnection.table("expense_event_category").build()

            val expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))

            // execute
            val actual = transaction { sut.findByExpenseId(expenseId = expenseId) }

            // assert
            val expected = ExpenseEvent(
                expenseEventId = ExpenseEventId(ULID.parseULID("01KDHVD5XTTR9XR4ZAFSSETGXS")),
                expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
                eventCategory = EventCategory.DELETE,
            )
            actual shouldBe expected

            assertThat(expenseIdTable).hasNumberOfRows(1)
            assertThat(expenseEventIdTable).hasNumberOfRows(2)
            assertThat(expenseEventTable).hasNumberOfRows(2)
            assertThat(expenseEventCategoryTable).hasNumberOfRows(2)
        }
    }
})
